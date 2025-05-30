package org.example.tshirtlabbackend.design.domain;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DesignDto {
    private Long id;
    private String createdAt;
    private String url;
}