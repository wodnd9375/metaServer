package com.kt.geniestore.meta.apkmanagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AppDTO {
    private String appName;
    private String packageName;
    private String versionName;
    private Long versionCode;
    private String appType;
    private String hasAds;
    private String limitedAge;
    private String description;
    private List<String> categories;
    private String company;
    private String developerName;
    private String phone;
    private String webSite;
    private String email;
}
