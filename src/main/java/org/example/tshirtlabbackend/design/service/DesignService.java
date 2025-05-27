package org.example.tshirtlabbackend.design.service;

import lombok.RequiredArgsConstructor;
import org.example.tshirtlabbackend.aws.S3StorageService;
import org.example.tshirtlabbackend.design.domain.Design;
import org.example.tshirtlabbackend.user.domain.User;
import org.example.tshirtlabbackend.design.repository.DesignRepository;
import org.example.tshirtlabbackend.aws.S3UrlFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DesignService {

    private final DesignRepository designRepository;
    private final S3UrlFactory s3UrlFactory;
    private final S3StorageService s3StorageService;

    /**
     * Persist the S3 design reference for the given user.
     * This is intentionally simple – uploading to S3 happens elsewhere.
     */
    @Transactional
    public Design saveDesign(User user, String s3Key) {
        String url = s3UrlFactory.toUrl(s3Key);
        Design design = Design.builder()
                .owner(user)
                .s3Key(s3Key)
                .url(url)
                .build();
        return designRepository.save(design);
    }

    @Transactional
    public Design saveDesign(User user, MultipartFile file) {
        String key = s3StorageService.upload(file);
        String url = s3StorageService.publicUrl(key);
        Design d = Design.builder()
                .owner(user)
                .s3Key(key)
                .url(url)
                .build();
        return designRepository.save(d);
    }

    public Optional<Design> findById(Long id) {
        return designRepository.findById(id);
    }

    /** List designs owned by the user */
    @Transactional(readOnly = true)
    public List<Design> listDesigns(User user) {
        return designRepository.findByOwner(user);
    }
}

