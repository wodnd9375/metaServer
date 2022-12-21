//package com.kt.geniestore.meta.apkmanagement.config;
//
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;

//@Configuration
//public class AwsConfig {

//    private final String iamAccessKey = "AKIAWGKW2X6QRAY3FWU5";
//    private final String iamSecretKey = "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";
//    private final String region = "ap-northeast-2";
//
//    @Bean
//    public AmazonS3Client amazonS3Client() {
//        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(iamAccessKey, iamSecretKey);
//        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
//                .withRegion(region)
//                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
//                .build();
//    }
//}
