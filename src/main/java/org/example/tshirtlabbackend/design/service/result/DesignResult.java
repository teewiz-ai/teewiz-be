package org.example.tshirtlabbackend.design.service.result;

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
public class DesignResult {
    private Long id;
    private String s3Key;
    private String url;
    private String prompt;
    private String style;
    private List<String> sampleImageUrls;
    private Instant createdAt;
}