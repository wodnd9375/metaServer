package com.kt.geniestore.meta.apkmanagement.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.geniestore.meta.apkmanagement.entity.App;
import com.kt.geniestore.meta.apkmanagement.entity.AppVersion;
import com.kt.geniestore.meta.apkmanagement.entity.Developer;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AppsResponse {
    private App app;
    @JsonProperty("versionList")
    private List<AppVersion> appVersion;
    private Developer developer;

    @Builder
    public AppsResponse(App app, List<AppVersion> appVersion, Developer developer) {
        this.app = app;
        this.appVersion = appVersion;
        this.developer = developer;
    }
}
