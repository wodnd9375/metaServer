package com.kt.geniestore.meta.apkmanagement.controller;

import com.kt.geniestore.meta.apkmanagement.common.response.*;
import com.kt.geniestore.meta.apkmanagement.dto.AppDTO;
import com.kt.geniestore.meta.apkmanagement.entity.DeveloperInfo;
import com.kt.geniestore.meta.apkmanagement.service.AppService;
import com.kt.geniestore.meta.apkmanagement.service.ServerInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AppRestController {
    // todo : policy 관리 (연령 제한, 단말 별 MR 차수 제한 등)

    private final AppService appService;

    @PostMapping(value = "/app/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse> uploadApp(@RequestPart MultipartFile uploadFile,
                                                    @RequestPart MultipartFile iconFile,
                                                    @RequestPart MultipartFile bannerFile,
                                                    @RequestPart List<MultipartFile> screenshot,
                                                    @RequestPart AppDTO appDTO,
                                                    @RequestPart("DeveloperInfo") DeveloperInfo developerInfo) throws Exception {

        CommonResponse response = new CommonResponse();


        appService.uploadApp(uploadFile, iconFile, bannerFile, screenshot, appDTO, developerInfo);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/app/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse> updateApp(@RequestPart MultipartFile uploadFile,
                                                          @RequestPart AppDTO appDTO) throws Exception {
        CommonResponse response = new CommonResponse();

        appService.updateApp();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value="/app/server")
    public ResponseEntity getDownloadList() {

        CommonResponse commonResponse = new CommonResponse();
        List<ServerInfo> serverInfo;
        DownloadListResponse downloadListResponse = new DownloadListResponse();
        Response response  = new Response();

        serverInfo = appService.discoveryClient();
        downloadListResponse.setCommonResponse(commonResponse);
        downloadListResponse.setServerInfo(serverInfo);

//        response.setCommonResponse(commonResponse);
//        response.setDownloadListResponse(downloadListResponse);

        return new ResponseEntity<>(downloadListResponse, HttpStatus.OK);
    }

    @GetMapping(value="/app")
    public ResponseEntity getAppsInfo() {

        CommonResponse commonResponse = new CommonResponse();
//        AllAppResponse appResponse;
        Response response = new Response();
//        appResponse = appService.getAllApps();

        List<AppsResponse> appResponse = appService.getAllApps();
//        commonResponse = appResponse;
        response.setCommonResponse(commonResponse);
        response.setAllAppResponse(appResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
