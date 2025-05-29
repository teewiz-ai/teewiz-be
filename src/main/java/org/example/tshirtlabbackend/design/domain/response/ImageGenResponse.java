package org.example.tshirtlabbackend.design.domain.response;

import lombok.Data;

import java.util.List;

@Data
public class ImageGenResponse {
    /**
     * Base64‐encoded images
     */
    private List<String> images;
}