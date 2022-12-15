package com.kt.geniestore.meta.apkmanagement.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity()
@Getter @Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="APPVERSION")
public class AppVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    private String versionName;

    private Long versionCode;

    private String fileName;

    private String checksum;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime regTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_name")
    @JsonIgnore
    private App app;

    @Builder
    public AppVersion(String versionName, Long versionCode, String fileName, String checksum, LocalDateTime regTime, App app) {
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.fileName = fileName;
        this.checksum = checksum;
        this.regTime = regTime;
        this.app = app;
    }
}