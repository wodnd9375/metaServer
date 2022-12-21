package com.kt.geniestore.meta.apkmanagement.service;

import com.kt.geniestore.meta.apkmanagement.Exception.DuplicateAppException;
import com.kt.geniestore.meta.apkmanagement.common.response.AllAppResponse;
import com.kt.geniestore.meta.apkmanagement.common.response.AppDownloadResponse;
import com.kt.geniestore.meta.apkmanagement.common.response.AppsResponse;
import com.kt.geniestore.meta.apkmanagement.dto.AppDTO;
import com.kt.geniestore.meta.apkmanagement.entity.*;
import com.kt.geniestore.meta.apkmanagement.repository.*;
import com.kt.geniestore.meta.apkmanagement.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.InputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppService {

    //    private final WebClient webClient;
//    private final AmazonS3Client amazonS3Client;
    private final AppRepository appRepository;
    private final AppVersionRepository appVersionRepository;
    private final AppCategoryMapRepository appCategoryMapRepository;
    private final CategoryRepository categoryRepository;
    private final DeveloperRepository developerRepository;

    private final DiscoveryClient discoveryClient;

    final FileUtils fileUtils = new FileUtils();

    @Value("${apk.dir.path}")
    private String dir;
    @Value("${apk.dir.userName}")
    private String userName;
    @Value("${cloud.aws.ec2.key}")
    private String privateKey;
    private static final Logger logger = LoggerFactory.getLogger(AppService.class);


    public AllAppResponse getAllApps() {
        AllAppResponse AllAppResponse = new AllAppResponse();
        List<AppsResponse> responseList = new ArrayList<>();
        List<App> apps = appRepository.findAll();

        for(App app : apps) {
            // todo : return 최신 버전
            AppVersion appVersion = appVersionRepository.findByApp(app).get(0);
            Developer developer = developerRepository.findByApp(app);
            List<AppCategoryMap> appCategoryMaps = appCategoryMapRepository.findByApp(app);

            List<String> categories = new ArrayList<>();
            for(AppCategoryMap map : appCategoryMaps) {
                Optional<Category> category = categoryRepository.findById(map.getCategory().getId());
                if(category.isPresent()) {
                    categories.add(category.get().getCategory());
                }
            }
            app.setCategories(categories);

            AppsResponse appResponse = AppsResponse.builder()
                    .app(app)
                    .appVersion(appVersion)
                    .developer(developer)
                    .build();

            responseList.add(appResponse);
        }

        AllAppResponse.setApps(responseList);

        return AllAppResponse;
    }

    public List<ServerInfo> discoveryClient () {
        List<ServiceInstance> instances = discoveryClient.getInstances("remote-repo");
        List<ServerInfo> serverInfo = new ArrayList<>();

        for(ServiceInstance instance : instances) {
            serverInfo.add(new ServerInfo(instance.getHost(), instance.getPort()));
//            serverInfo.add(instance.getHost());
        }

        return serverInfo;
    }

    @Transactional
    public void uploadApp(MultipartFile uploadFile, MultipartFile iconFile, MultipartFile bannerFile, List<MultipartFile> screenshots, AppDTO appDTO, DeveloperInfo developerInfo) throws Exception {

        //  DB 중복 체크 - 중복 조건 : packaName + versionCode + versionName
        App app = appRepository.findByPackageName(appDTO.getPackageName());

        if(app != null) {
            if(checkDuplicateVersion(app, appDTO.getVersionName(), appDTO.getVersionCode())) {
                throw new DuplicateAppException();
            }
        }

        String checksum = toHex(checksum(uploadFile.getInputStream()));

        try {
            uploadRepo(uploadFile, iconFile, bannerFile, screenshots, appDTO);
        } catch(Exception e) {

        }

        List<String> uploadSceeenshots = new ArrayList<>();
        for(MultipartFile screenshot : screenshots) {
            uploadSceeenshots.add(screenshot.getOriginalFilename());
        }
        // App DB 저장
        App uploadApp = saveApp(appDTO, uploadFile.getOriginalFilename(), checksum, iconFile.getOriginalFilename(), bannerFile.getOriginalFilename(), uploadSceeenshots, userName);

        // AppCategoryMap DB 저장
        List<String> categories = appDTO.getCategories();
        saveAppCategory(categories, uploadApp);

        saveDeveloper(developerInfo, app);
    }

    @Transactional
    public void updateApp() {

    }


    public boolean uploadRepo(MultipartFile mfile, MultipartFile iconFile, MultipartFile bannerFile,
                             List<MultipartFile> screenshots, AppDTO appDTO) {

        String appPath = "/" + appDTO.getAppName() + "/" + appDTO.getVersionCode() + "/" + "app" + "/";
        String iconPath = "/" + appDTO.getAppName() + "/" + appDTO.getVersionCode() + "/" + "icon" + "/";
        String bannerPath = "/" + appDTO.getAppName() + "/" + appDTO.getVersionCode() + "/" + "banner" + "/";
        String screenPath = "/" + appDTO.getAppName() + "/" + appDTO.getVersionCode() + "/" + "screenshot" + "/";


        for(ServerInfo serverInfo : discoveryClient()) {
            logger.info("server info : " + serverInfo.getHost());
            try {
                fileUtils.init(serverInfo.getHost(), userName, null, 22, privateKey);

                fileUtils.upload(dir, appPath, mfile);
                fileUtils.upload(dir, iconPath, iconFile);
                fileUtils.upload(dir, bannerPath, bannerFile);

                for(MultipartFile screenshot : screenshots) {
                    fileUtils.upload(dir, screenPath, screenshot);
                }

            } catch (Exception e) {
                return false;
            }
        }

        fileUtils.disconnection();

        return true;
    }

    private boolean checkDuplicateVersion(App app, String versionName, Long versionCode) {
        List<AppVersion> versions = appVersionRepository.findByApp(app);
        for(AppVersion av : versions) {
            if(av.getVersionCode().equals(versionCode) && av.getVersionName().equals(versionName)) {
                // todo : response 정의 (버전 중복)
                logger.info("duplicate Version...");
                return true;
            }
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public App saveApp(AppDTO appDTO, String fileName, String checksum,
                        String iconFileName, String bannerFileName, List<String> screenshots, String userName) {
        List<AppVersion> versions = new ArrayList<>();

        App app = new App();
        app.setAppName(appDTO.getAppName());
        app.setPackageName(appDTO.getPackageName());
        app.setAppType(appDTO.getAppType());
        app.setHasAds(appDTO.getHasAds());
        app.setLimitedAge(appDTO.getLimitedAge());
        app.setDescription(appDTO.getDescription());

        AppVersion appVersion = AppVersion.builder()
                .versionName(appDTO.getVersionName())
                .versionCode(appDTO.getVersionCode())
                .fileName(fileName)
                .checksum(checksum)
                .regTime(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .app(app)
                .build();

        versions.add(appVersion);
        app.setVersions(versions);

        app.setIconFile(iconFileName);
        app.setBannerFile(bannerFileName);
        app.setScreenshots(screenshots);

        appRepository.save(app);

        return app;

    }

    @Transactional(rollbackFor = Exception.class)
    public void saveAppCategory(List<String> categories, App app) {
        for (String category : categories) {
            Category existsCategory = categoryRepository.findByCategory(category);
            if (existsCategory != null) {
                AppCategoryMap appCategoryMap = AppCategoryMap.builder()
                        .app(app)
                        .category(existsCategory)
                        .build();
                appCategoryMapRepository.save(appCategoryMap);
            } else {

            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveDeveloper(DeveloperInfo developerInfo, App app) {
        Developer developer = Developer.builder()
                .company(developerInfo.getCompany())
                .developerName(developerInfo.getName())
                .phone(developerInfo.getPhone())
                .email(developerInfo.getEmail())
                .app(app)
                .build();

        developerRepository.save(developer);
    }

    private String toHex(byte[] bytes){
        return DatatypeConverter.printHexBinary(bytes);
    }

    private byte[] checksum(InputStream input) {
        try (InputStream in = input) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256"); // MD5, SHA1, SHA-256, SHA-512
            byte[] block = new byte[4096];
            int length;
            while ((length = in.read(block)) > 0) {
                digest.update(block, 0, length);
            }
            return digest.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
