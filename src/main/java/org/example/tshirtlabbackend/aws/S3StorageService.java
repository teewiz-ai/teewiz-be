package org.example.tshirtlabbackend.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3;

    @Value("${app.aws.bucket}")
    private String bucket;

    @Value("${app.aws.base-url}")
    private String baseUrl;


    public String upload(String key, byte[] data) {
        s3.putObject(PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType("image/png")
                        .build(),
                RequestBody.fromBytes(data));

        return "%s/%s".formatted(baseUrl, key);
    }

}
