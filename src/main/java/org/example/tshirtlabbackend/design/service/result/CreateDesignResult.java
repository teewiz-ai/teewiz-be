package org.example.tshirtlabbackend.design.service.result;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDesignResult {
    private Long id;
    private String s3Key;
    private String url;
    private String prompt;
    private String style;
    private String sampleImageUrls;
    private Instant createdAt;
}