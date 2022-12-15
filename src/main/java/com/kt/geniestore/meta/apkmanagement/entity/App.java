package com.kt.geniestore.meta.apkmanagement.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name="APP")
public class App {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    private String appName;
    private String appType;
    private String limitedAge;
    private String hasAds;
    private String description;

    private String iconFile;
    private String bannerFile;

    @ElementCollection
    private List<String> screenshots = new ArrayList<>();

    @Column(name = "package_name")
    private String packageName;


    @JsonIgnore
    @OneToMany(mappedBy = "app", cascade = CascadeType.ALL)
    private List<AppVersion> versions = new ArrayList<>();

    @Transient
    private List<String> categories = new ArrayList<>();

    @Builder
    public App(String appName, String appType, String limitedAge, String hasAds, String description, String packageName, List<AppVersion> versions,
               String iconFile, String bannerFile, List<String> screenshots) {
        this.appName = appName;
        this.appType = appType;
        this.limitedAge = limitedAge;
        this.hasAds = hasAds;
        this.description = description;
        this.packageName = packageName;
        this.versions = versions;
        this.iconFile = iconFile;
        this.bannerFile = bannerFile;
        this.screenshots = screenshots;
    }
}