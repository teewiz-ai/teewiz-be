package org.example.tshirtlabbackend.images.controller;

import lombok.RequiredArgsConstructor;
import org.example.tshirtlabbackend.aws.S3StorageService;
import org.example.tshirtlabbackend.images.dto.PresignRequest;
import org.example.tshirtlabbackend.images.dto.PresignResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {
    private final S3StorageService s3;

    @PostMapping("/presign")
    public PresignResponse presign(@RequestBody PresignRequest req) {
        String key = "uploads/" + UUID.randomUUID() + "-" + req.getFilename();
        URL presignUrl = s3.generatePresignedUploadUrl(key, req.getContentType(), Duration.ofMinutes(10));
        String publicUrl = s3.getPublicUrl(key);
        return new PresignResponse(presignUrl.toString(), key, publicUrl);
    }

}