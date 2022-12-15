package com.kt.geniestore.meta.apkmanagement.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.geniestore.meta.apkmanagement.entity.AppVersion;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.List;

@Getter
@Setter
public class AppDownloadResponse extends CommonResponse {
    @JsonProperty("data")
    private AppVersion appVersion;
    // appCategory
    // iconFile
    // ic

}

