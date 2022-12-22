package com.kt.geniestore.meta.apkmanagement.common.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AllAppResponse {
    private List<AppsResponse> apps;
}
