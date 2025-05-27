package org.example.tshirtlabbackend.design.domain;


public record DesignDto(Long id, String url, String createdAt) {
    public static DesignDto from(Design d) {
        return new DesignDto(d.getId(), d.getUrl(), d.getCreatedAt().toString());
    }
}