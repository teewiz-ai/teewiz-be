package org.example.tshirtlabbackend.design.service;

import lombok.RequiredArgsConstructor;
import org.example.tshirtlabbackend.aws.S3StorageService;
import org.example.tshirtlabbackend.design.domain.Design;
import org.example.tshirtlabbackend.design.domain.DesignDto;
import org.example.tshirtlabbackend.user.domain.User;
import org.example.tshirtlabbackend.design.repository.DesignRepository;
import org.example.tshirtlabbackend.aws.S3UrlFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DesignService {

    private final DesignRepository designRepository;
    private final S3UrlFactory s3UrlFactory;
    private final S3StorageService s3StorageService;

    @Transactional
    public Design saveDesign(User user, byte[] imageBytes) {
        String key = "designs/%s.png".formatted(UUID.randomUUID());
        String url = s3StorageService.upload(key, imageBytes);

        Design d = new Design();
        d.setOwner(user);
        d.setS3Key(key);
        d.setUrl(url);
        return designRepository.save(d);
    }

    public Optional<Design> findById(Long id) {
        return designRepository.findById(id);
    }

    /** List designs owned by the user */
    @Transactional(readOnly = true)
    public List<DesignDto> listDesigns(User user) {
        List<Design> designs = designRepository.findByOwner(user);
        return designs.stream()
                .map(d -> new DesignDto(
                        d.getId(),
                        d.getCreatedAt().toString(),
                        d.getUrl()
                ))
                .collect(Collectors.toList());
    }
}

