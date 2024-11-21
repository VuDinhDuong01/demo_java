package com.example.demo.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class MinioConfig {
    @Value("${spring.minio.url}")
    private String url;

    @Value("${spring.minio.accessKey_minio}")
    private String accessKey;

    @Value("${spring.minio.secretKey_minio}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        System.out.println("url" + url);
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey,
                        secretKey)
                .build();
    }
}
