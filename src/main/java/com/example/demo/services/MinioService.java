package com.example.demo.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MinioService {
    @Value("${spring.minio.bucket_name}")
    String bucketName;

    @Autowired
    MinioClient minioClient;

    public String uploadFile(MultipartFile files) {

        try (InputStream inputStream = files.getInputStream()) {
            try {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(files.getOriginalFilename())
                        .stream(inputStream, inputStream.available(), -1)
                        .build());
                return minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder().bucket(bucketName).method(Method.GET).expiry(3600)
                                .object(files.getOriginalFilename())
                                .extraQueryParams(Map.of("response-content-type", "image/png")).build());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
