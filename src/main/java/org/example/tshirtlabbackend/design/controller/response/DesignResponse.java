package org.example.tshirtlabbackend.design.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DesignResponse {
    private Long id;
    private String s3Key;
    private String url;
    private String prompt;
    private String style;
    private List<String> sampleImageUrls;
    private Instant createdAt;
}