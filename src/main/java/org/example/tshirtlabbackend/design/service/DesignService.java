package org.example.tshirtlabbackend.design.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tshirtlabbackend.common.PaginatedResult;
import org.example.tshirtlabbackend.config.aws.S3StorageService;
import org.example.tshirtlabbackend.design.controller.request.ImageGenRequest;
import org.example.tshirtlabbackend.design.data.entity.Design;
import org.example.tshirtlabbackend.design.mapper.DesignMapper;
import org.example.tshirtlabbackend.design.service.command.CreateDesignCommand;
import org.example.tshirtlabbackend.design.service.command.SearchDesignCommand;
import org.example.tshirtlabbackend.design.service.result.CreateDesignResult;
import org.example.tshirtlabbackend.design.service.result.DesignResult;
import org.example.tshirtlabbackend.llm.LLMService;
import org.example.tshirtlabbackend.user.domain.User;
import org.example.tshirtlabbackend.design.data.repository.DesignRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DesignService {

    private final DesignRepository designRepository;
    private final LLMService llmService;
    private final S3StorageService s3StorageService;

    /**
     * Generate a new design using the LLM service, save it to S3,
     * and create a Design entity in the database.
     */
    @Transactional
    public CreateDesignResult createDesign(CreateDesignCommand cmd) {
        byte[] imageBytes = llmService.generateImage(
                ImageGenRequest.builder()
                        .prompt(cmd.getPrompt())
                        .size(cmd.getSize())
                        .quality(cmd.getQuality())
                        .background(cmd.getBackground())
                        .format(cmd.getFormat())
                        .n(cmd.getN())
                        .sampleImageUrls(cmd.getSampleImageUrls())
                        .build()
        );

        String key = "designs/%s.png".formatted(UUID.randomUUID());
        String url = s3StorageService.upload(key, imageBytes);

        String joinedSamples = (cmd.getSampleImageUrls() == null || cmd.getSampleImageUrls().isEmpty())
                ? ""
                : String.join(",", cmd.getSampleImageUrls());

        Design entity = Design.builder()
                .owner(cmd.getOwner())
                .s3Key(key)
                .url(url)
                .prompt(cmd.getPrompt())
                .style(cmd.getStyle())
                .sampleImageUrls(joinedSamples)
                .build();

        entity = designRepository.save(entity);

        return DesignMapper.INSTANCE.toCreateResult(entity);
    }

    /**
     * Fetch a single Design by ID & Owner. Return a SearchDesignResult.
     */
    @Transactional(readOnly = true)
    public DesignResult getDesign(Long designId, User owner) {
        Design d = designRepository.findByIdAndOwner(designId, owner)
                .orElseThrow(() -> new IllegalArgumentException("Design not found"));
        return DesignMapper.INSTANCE.toResult(d);
    }


    /**
     * Delete a Design (remove from S3 and from DB).
     */
    @Transactional
    public void deleteDesign(Long designId, User owner) {
        Design existing = designRepository.findByIdAndOwner(designId, owner)
                .orElseThrow(() -> new IllegalArgumentException("Design not found"));

//        s3StorageService.delete(existing.getS3Key());
        designRepository.delete(existing);
    }

    /**
     * Paginated list of this user's designs.
     */
    @Transactional(readOnly = true)
    public PaginatedResult<DesignResult> searchDesigns(SearchDesignCommand cmd) {
        Pageable pageable = PageRequest.of(cmd.getPage() - 1, cmd.getPageSize(), Sort.by("createdAt").descending());

        Page<Design> page = designRepository.findByOwner(cmd.getOwner(), pageable);
        List<DesignResult> results = page.getContent().stream()
                .map(DesignMapper.INSTANCE::toResult)
                .toList();
        return createPaginatedResult(page, results);
    }

    private <T> PaginatedResult<T> createPaginatedResult(
            Page<?> page,
            List<T> results
    ) {
        PaginatedResult<T> result = new PaginatedResult<>();
        result.setItems(results);
        result.setPage(page.getNumber());
        result.setSize((int) page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setTotalItems(page.getTotalElements());
        return result;
    }
}

