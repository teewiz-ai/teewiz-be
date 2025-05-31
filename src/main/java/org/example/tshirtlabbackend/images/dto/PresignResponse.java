package org.example.tshirtlabbackend.images.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresignResponse {
    private String presignUrl;
    private String fileKey;
    private String publicUrl;
}