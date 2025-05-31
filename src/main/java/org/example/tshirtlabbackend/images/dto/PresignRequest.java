package org.example.tshirtlabbackend.images.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresignRequest {
    private String filename;
    private String contentType;
}