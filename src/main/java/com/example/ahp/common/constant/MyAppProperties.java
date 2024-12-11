package com.example.ahp.common.constant;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class MyAppProperties {
    @Value("${my-app-properties.minio.endpoint}")
    private String minioEndpoint;
    @Value("${my-app-properties.minio.accessKey}")
    private String minioAccessKey;
    @Value("${my-app-properties.minio.secretKey}")
    private String minioSecretKey;
}
