package com.kt.geniestore.meta.apkmanagement.common.response;

import com.kt.geniestore.meta.apkmanagement.service.ServerInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DownloadListResponse extends CommonResponse {
    private List<ServerInfo> serverInfo;
}

