package com.kt.geniestore.meta.apkmanagement.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    @JsonProperty("meta")
    private CommonResponse commonResponse;
    @JsonProperty("data")
    private List<AppsResponse> allAppResponse;
    @JsonProperty("serverInfo")
    private DownloadListResponse downloadListResponse;
}
