package com.kt.geniestore.meta.apkmanagement.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.geniestore.meta.apkmanagement.service.ServerInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DownloadListResponse {
    @JsonProperty("meta")
    private CommonResponse commonResponse;
    @JsonProperty("data")
    private List<ServerInfo> serverInfo;
}

