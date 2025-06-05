package org.example.tshirtlabbackend.design.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request payload for generating a design.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateDesignRequest {

    /** The text prompt for generation or edit. */
    private String prompt;

    /** Optional style hint (e.g. "retro neon", "photorealistic"). */
    private String style;

    /** How many images to return (default 1). */
    @Builder.Default
    private Integer n = 1;

    /** Desired image size: 1024x1024, 1536x1024, 1024x1536, or "auto". */
    private String size;

    /** Rendering quality: low, medium, high, or auto. */
    private String quality;

    /** Background: transparent, opaque, or auto. */
    private String background;

    /** Output format: png, jpeg, or webp. */
    private String format;

    /** Public S3 URLs of the uploaded sample images. */
    private List<String> sampleImageUrls;
}
