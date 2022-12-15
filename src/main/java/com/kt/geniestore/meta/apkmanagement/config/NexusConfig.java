package com.kt.geniestore.meta.apkmanagement.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class NexusConfig {

    @Bean
    public WebClient nexusWebClient() {
        return WebClient.builder().baseUrl("http://127.0.0.1:8082/nexus").build();
    }

}