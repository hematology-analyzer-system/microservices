package com.example.user.controller;

import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UploadController {

    private final S3Client s3Client;

    @Value("${spaces.bucket-name}")
    private String bucketName;

    @Value("${spaces.endpoint}")
    private String endpoint;

    public UploadController(@Value("${spaces.access-key}") String accessKey,
                            @Value("${spaces.secret-key}") String secretKey,
                            @Value("${spaces.endpoint}") String endpoint) {
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1) // still required
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @PostMapping("/upload")
    public Object upload(@RequestParam("file") MultipartFile file) throws IOException, java.io.IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .acl("public-read")
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        String publicUrl = endpoint + "/" + fileName;

        return Collections.singletonMap("url", publicUrl);
    }
}
