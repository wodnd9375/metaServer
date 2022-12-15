package com.kt.geniestore.meta.apkmanagement.controller;

import com.kt.geniestore.meta.apkmanagement.common.CustomException;
import com.kt.geniestore.meta.apkmanagement.common.request.AppDownloadRequest;
import com.kt.geniestore.meta.apkmanagement.common.response.AllAppResponse;
import com.kt.geniestore.meta.apkmanagement.common.response.AppDownloadResponse;
import com.kt.geniestore.meta.apkmanagement.common.response.CommonResponse;
import com.kt.geniestore.meta.apkmanagement.common.response.DownloadListResponse;
import com.kt.geniestore.meta.apkmanagement.dto.AppDTO;
import com.kt.geniestore.meta.apkmanagement.service.AppService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AppRestController {
    // todo : policy 관리 (연령 제한, 단말 별 MR 차수 제한 등)

    private final AppService appService;

    private static final Logger logger = LoggerFactory.getLogger(AppRestController.class);

    @PostMapping(value = "/app/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponse> uploadComponent(@RequestPart MultipartFile file,
                                                          @RequestPart MultipartFile iconFile,
                                                          @RequestPart MultipartFile bannerFile,
                                                          @RequestPart List<MultipartFile> screenshot,
                                                          @RequestPart AppDTO appDTO) throws IOException, CustomException {

        CommonResponse response = new CommonResponse();

        appService.uploadComponent(file, iconFile, bannerFile, screenshot, appDTO);

        return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/app/download")
    public ResponseEntity downloadComponent(@ModelAttribute AppDownloadRequest downloadReq,
                                            @RequestParam(required = false) String versionName,
                                            @RequestParam(required = false) Long versionCode) {

        CommonResponse response = null;

//        AppDownloadResponse downloadResponse = appService.getDownloadUrl(downloadReq.getPackageName(), versionName, versionCode, downloadReq.getTrxId());
//        response = downloadResponse;

        return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);
    }

    @GetMapping(value="/app/server")
    public ResponseEntity getDownloadList() {

        CommonResponse response = null;
        List<String> serverInfo = new ArrayList<>();
        DownloadListResponse downloadListResponse = new DownloadListResponse();

        serverInfo = appService.discoveryClient();
        downloadListResponse.setServerInfo(serverInfo);
        response = downloadListResponse;

        return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);
    }

    @GetMapping(value="/app")
    public ResponseEntity getAppsInfo() {

        CommonResponse response = null;
        AllAppResponse appResponse = null;

        appResponse = appService.getAllApps();

        response = appResponse;
        return new ResponseEntity<CommonResponse>(response, HttpStatus.OK);
    }
}
