package com.kt.geniestore.meta.apkmanagement.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kt.geniestore.meta.apkmanagement.entity.AppVersion;
import com.kt.geniestore.meta.apkmanagement.entity.Category;
import com.kt.geniestore.meta.apkmanagement.entity.Developer;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class AllAppResponse extends CommonResponse {
    @JsonProperty("data")
    private List<AppsResponse> apps;
}
