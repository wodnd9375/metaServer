package com.kt.geniestore.meta.apkmanagement.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name="DEVELOPER")
public class Developer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    @Column(name="developer_id")
    private Long id;

    private String company;
    @JsonProperty("name")
    private String developerName;
    private String phone;
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_name")
    @JsonIgnore
    private App app;

    @Builder
    public Developer(String company, String developerName, String phone, String email, App app) {
        this.company = company;
        this.developerName = developerName;
        this.phone = phone;
        this.email = email;
        this.app = app;
    }
}