package com.kt.geniestore.meta.apkmanagement.common.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppDownloadRequest {
    private String packageName;
    private String trxId;
}
