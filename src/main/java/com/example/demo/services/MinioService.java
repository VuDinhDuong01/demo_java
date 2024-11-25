package com.example.demo.services;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.AuthEntity;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.repositorys.AuthRepository;
import com.example.demo.utils.Utils;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
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

    @Autowired
    AuthRepository authRepository;

    public List<Map<String, String>> uploadFile(List<MultipartFile> files) {
        List<Map<String, String>> listUrl = new ArrayList<>();
        List<String> signature = new ArrayList<>();
        String user_id = Utils.getUserId();
        AuthEntity authEntity = authRepository.findById(user_id).orElse(null);
        for (MultipartFile file : files) {
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "/signature/" + user_id;
            signature.add(fileName);
            try (InputStream inputStream = file.getInputStream()) {
                System.out.println("file name:" + file.getOriginalFilename());
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(inputStream, inputStream.available(), -1)
                        .build());

            } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
                    | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
                    | IllegalArgumentException | IOException e) {
                e.printStackTrace();
            }
            try {
                String url = minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder().bucket(bucketName).method(Method.GET).expiry(3600)
                                .object(fileName)
                                .extraQueryParams(Map.of("response-content-type", "image/png")).build());
                Map<String, String> map = new HashMap<>();
                map.put("url", url);
                map.put("type", "image");
                listUrl.add(map);
            } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
                    | InvalidResponseException | NoSuchAlgorithmException | XmlParserException | ServerException
                    | IllegalArgumentException | IOException e) {
                e.printStackTrace();
            }
        }
        authEntity.setSignature(signature);
        authRepository.save(authEntity);

        return listUrl;
    }

    public List<String> getUrl(String userId) {
        AuthEntity user = authRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new NotFoundException("user not found");
        }
        List<String> signatures = user.getSignature();
        List<String> urls = new ArrayList<>();
        if (signatures.size() == 0)
            return new ArrayList<>();
        System.out.println("signature:" + signatures);
        for (String signature : signatures) {
            try {
                String url = minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs
                                .builder()
                                .method(Method.GET)
                                .bucket(bucketName)
                                .expiry(3600)
                                .object(signature)
                                .extraQueryParams(Map.of("response-content-type", "image/png"))
                                .build());
                urls.add(url);
            } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
                    | InvalidResponseException | NoSuchAlgorithmException | XmlParserException | ServerException
                    | IllegalArgumentException | IOException e) {

                e.printStackTrace();
            }
        }
        return urls;

    }
}
