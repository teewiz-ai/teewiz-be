package org.example.tshirtlabbackend.config.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Central place to turn an S3 object key into a HTTP‑loadable URL.
 * Swap implementation if you move to CloudFront or presigned URLs.
 */
@Component
public class S3UrlFactory {

    @Value("${app.aws.bucket}")
    private String bucketName;

    @Value("${app.aws.region}")
    private String region;

    public String toUrl(String s3Key) {
        return "https://%s.s3.%s.amazonaws.com/%s".formatted(bucketName, region, s3Key);
    }
}

