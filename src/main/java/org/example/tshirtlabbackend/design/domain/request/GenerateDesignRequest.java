package org.example.tshirtlabbackend.design.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for generating or editing a design.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerateDesignRequest {
    /**
     * The text prompt for generation or edit.
     */
    private String prompt;

    /**
     * Optional style hint (e.g. "retro neon", "photorealistic").
     */
    private String style;

    /**
     * How many images to return (default 1).
     */
    @Builder.Default
    private Integer n = 1;

    /**
     * Desired image size: 1024x1024, 1536x1024, 1024x1536, or "auto".
     */
    private String size;

    /**
     * Rendering quality: low, medium, high, or auto.
     */
    private String quality;

    /**
     * Background: transparent, opaque, or auto.
     */
    private String background;

    /**
     * Output format: png, jpeg, or webp.
     */
    private String format;

    /**
     * Compression level for JPEG/WebP (0–100).
     */
    private Integer outputCompression;

    /**
     * Public S3 URL of the uploaded sample image.
     */
    private String sampleImageUrl;
}
