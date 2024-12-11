package com.example.ahp.config;

import com.example.ahp.common.constant.MyAppProperties;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Autowired
    private MyAppProperties properties;

    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
                .endpoint(properties.getMinioEndpoint())
                .credentials(properties.getMinioAccessKey(), properties.getMinioSecretKey())
                .build();
    }
}
