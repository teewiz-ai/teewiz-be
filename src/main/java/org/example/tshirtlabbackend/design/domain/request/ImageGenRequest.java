package org.example.tshirtlabbackend.design.domain.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ImageGenRequest {
    private String prompt;
    private Integer n;
    private String size;
    private String format;
    private String background;
    private String quality;
    private List<String> sampleImageUrls;
}