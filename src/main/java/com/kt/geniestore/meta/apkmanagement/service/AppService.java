package com.kt.geniestore.meta.apkmanagement.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
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
import java.io.IOException;
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
    private final AmazonS3Client amazonS3Client;
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
    @Value("${apk.dir.password}")
    private String password;
    private String privateKey = "/Users/a10150541/Desktop/MetaServer/apkmanagement/genieStoreKey.pem";
    private static final Logger logger = LoggerFactory.getLogger(AppService.class);

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

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

    public List<String> discoveryClient () {
        List<ServiceInstance> instances = discoveryClient.getInstances("remote-repo");
        List<String> serverInfo = new ArrayList<>();

        for(ServiceInstance instance : instances) {
//            serverInfo.add(instance.getUri().toString());
            serverInfo.add(instance.getHost());
        }

        return serverInfo;
    }

    @Transactional
    public boolean uploadComponent(MultipartFile mfile, MultipartFile iconFile, MultipartFile bannerFile, List<MultipartFile> screenshots, AppDTO appDTO) throws IOException {

        String appName = appDTO.getAppName();
        String packageName = appDTO.getPackageName();
        String versionName = appDTO.getVersionName();
        long versionCode = appDTO.getVersionCode();
        String appType = appDTO.getAppType();
        String hasAds = appDTO.getHasAds();
        String limitedAge = appDTO.getLimitedAge();
        String description = appDTO.getDescription();
        List<String> categories = appDTO.getCategories();
        String company = appDTO.getCompany();
        String developerName = appDTO.getDeveloperName();
        String phone = appDTO.getPhone();
        String webSite = appDTO.getWebSite();
        String email = appDTO.getEmail();

        //  DB 중복 체크 - 중복 조건 : packaName + versionCode + versionName
        App app = appRepository.findByPackageName(packageName);

        if(app != null) {
            if(checkDuplicateVersion(app, versionName, versionCode)) {
                return false;
            }
        }


        String originalName = mfile.getName();
        String path = appName + "/" + versionCode + "/" + originalName;
        String checksum = toHex(checksum(mfile.getInputStream()));

        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(mfile.getContentType());
        objectMetaData.setContentLength(mfile.getSize());

        // S3 저장
//        try (InputStream inputStream = mfile.getInputStream()) {
//            amazonS3Client.putObject(new PutObjectRequest(bucketName, path, inputStream, objectMetaData)
//                    .withCannedAcl(CannedAccessControlList.PublicRead));
//        } catch (IOException e) {
//            return false;
//        }

        // EC2 저장
        List<String> serverInfoList = discoveryClient();
        try {
            uploadEC2(mfile, appName, packageName, versionName, versionCode, serverInfoList, iconFile, bannerFile, screenshots);
        } catch(Exception e) {
            return false;
        }


        List<String> screen = new ArrayList<>();
        for(MultipartFile screenshot : screenshots) {
            screen.add(screenshot.getOriginalFilename());
        }
        // App DB 저장
        App uploadApp = saveApp(appName, packageName, appType, hasAds, limitedAge, description, versionName, versionCode, mfile.getOriginalFilename(), checksum,
                iconFile.getOriginalFilename(), bannerFile.getOriginalFilename(), screen);


        // AppCategoryMap DB 저장
        saveAppCategory(categories, uploadApp);

        // Developer DB 저장
        saveDeveloper(company, developerName, phone, webSite, email, uploadApp);

        return true;
    }


    public boolean uploadEC2(MultipartFile mfile, String appName, String packageName, String versionName, Long versionCode, List<String> serverInfoList,
                             MultipartFile iconFile,
                             MultipartFile bannerFile,
                             List<MultipartFile> screenshots) {

        String appPath = "/" + appName + "/" + versionCode + "/" + "app" + "/";
        String iconPath = "/" + appName + "/" + versionCode + "/" + "icon" + "/";
        String bannerPath = "/" + appName + "/" + versionCode + "/" + "banner" + "/";
        String screenPath = "/" + appName + "/" + versionCode + "/" + "screenshot" + "/";

        for(String server : serverInfoList) {
            logger.info("server info : " + server);
            try {
                fileUtils.init(server, userName, null, 22, privateKey);

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

    public AppDownloadResponse getDownloadInfo(String packageName, String versionName, Long versionCode, String trxId) {
        AppDownloadResponse response = new AppDownloadResponse();

        List<String> urlList = new ArrayList<>();

//        App app = appRepository.findByPackageName(packageName);
//        if(app != null) {
//            if (versionName != null && versionCode != null) {
//                List<AppVersion> versions = appVersionRepository.findByApp(app);
//                AppVersion targetVersion = versions.get(0);
//
//                urlList.add(targetVersion.getUrl());
//                targetVersion.setDownloadUrl(urlList);
//                targetVersion.setDownloadUrl(urlList);
//                response.setAppVersion(targetVersion);
//
//            } else {
//                AppVersion targetVersion = app.getVersions().get(app.getVersions().size() - 1);
//                urlList.add(targetVersion.getUrl());
//                targetVersion.setDownloadUrl(urlList);
//                response.setAppVersion(targetVersion);
//            }
//        }
//        response.setTrxid(trxId);

        return response;
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

    @Transactional
    public App saveApp(String appName, String packageName, String appType, String hasAds, String limitedAge, String description, String versionName, Long versionCode, String fileName, String checksum,
                        String iconFileName, String bannerFileName, List<String> screenshots) {
        List<AppVersion> versions = new ArrayList<>();

        App app = new App();
        app.setAppName(appName);
        app.setPackageName(packageName);
        app.setAppType(appType);
        app.setHasAds(hasAds);
        app.setLimitedAge(limitedAge);
        app.setDescription(description);

        AppVersion appVersion = AppVersion.builder()
                .versionName(versionName)
                .versionCode(versionCode)
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

    @Transactional
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
                // todo : 카테고리 오류 예외처리

            }
        }
    }

    @Transactional
    public void saveDeveloper(String company, String developerName, String phone, String webSite, String email, App app) {
        Developer developer = Developer.builder()
                .company(company)
                .developerName(developerName)
                .phone(phone)
                .webSite(webSite)
                .email(email)
                .app(app)
                .build();

        developerRepository.save(developer);
    }

}
