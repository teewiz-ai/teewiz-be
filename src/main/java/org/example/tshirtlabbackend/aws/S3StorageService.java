// S3StorageService.java
package org.example.tshirtlabbackend.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3;
    private final S3Presigner presigner;

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
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(data));
        return "%s/%s".formatted(baseUrl, key);
    }

    /**
     * Generate a pre-signed S3 PUT URL for browser uploads, using the given contentType.
     */
    public URL generatePresignedUploadUrl(String key, String contentType, Duration duration) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignReq = PutObjectPresignRequest.builder()
                .putObjectRequest(putRequest)
                .signatureDuration(duration)
                .build();

        return presigner.presignPutObject(presignReq).url();
    }

    public String getPublicUrl(String key) {
        return "%s/%s".formatted(baseUrl, key);
    }
}
