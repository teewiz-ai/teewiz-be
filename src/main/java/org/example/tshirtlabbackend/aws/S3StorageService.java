package org.example.tshirtlabbackend.aws;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3;
    private final S3Presigner presigner;
    private final S3UrlFactory urlFactory;

    private final String bucket = "my-tshirt-designs";   // could @Value-inject

    /** Uploads the file and returns the S3 object key. */
    public String upload(MultipartFile file) {
        String ext = switch (file.getContentType()) {
            case MediaType.IMAGE_PNG_VALUE -> ".png";
            case MediaType.IMAGE_JPEG_VALUE -> ".jpg";
            default -> "";
        };
        String key = "designs/" + UUID.randomUUID() + ext;

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        try {
            s3.putObject(req, RequestBody.fromInputStream(
                    file.getInputStream(), file.getSize()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload to S3", e);
        }
        return key;
    }

    /** Streams the object back (for direct download through Spring MVC). */
    public GetObjectResponseWithStream download(String key) {
        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return new GetObjectResponseWithStream(
                s3.getObject(req),                     // InputStream
                s3.getObject(req).response()           // metadata
        );
    }

    /** Generates a time-limited HTTPS URL (front-end fetches directly). */
    public URL presignGet(String key, Duration ttl) {
        GetObjectRequest getObject = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presign = GetObjectPresignRequest.builder()
                .signatureDuration(ttl)
                .getObjectRequest(getObject)
                .build();

        return presigner.presignGetObject(presign).url();
    }

    /** Convenience – public URL when the bucket/object is world-readable. */
    public String publicUrl(String key) {
        return urlFactory.toUrl(key);
    }

    /* Nested helper record */
    public record GetObjectResponseWithStream(
            software.amazon.awssdk.core.ResponseInputStream<GetObjectResponse> stream,
            GetObjectResponse metadata) { }
}
