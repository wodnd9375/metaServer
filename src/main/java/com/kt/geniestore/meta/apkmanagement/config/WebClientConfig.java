package com.kt.geniestore.meta.apkmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.create("http://127.0.0.1:8787");
//        return WebClient.create("http://54.64.66.159:8787");
    }
}
