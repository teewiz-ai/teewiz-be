package org.example.tshirtlabbackend.design.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tshirtlabbackend.aws.S3StorageService;
import org.example.tshirtlabbackend.design.domain.Design;
import org.example.tshirtlabbackend.design.domain.DesignDto;
import org.example.tshirtlabbackend.design.domain.SaveDesignRequest;
import org.example.tshirtlabbackend.design.domain.request.GenerateDesignRequest;
import org.example.tshirtlabbackend.design.domain.response.GenerateDesignResponse;
import org.example.tshirtlabbackend.design.repository.DesignRepository;
import org.example.tshirtlabbackend.design.service.DesignService;
import org.example.tshirtlabbackend.llm.LLMService;
import org.example.tshirtlabbackend.user.domain.User;
import org.example.tshirtlabbackend.user.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/designs")
@RequiredArgsConstructor
public class DesignController {

    private final DesignService designService;
    private final UserRepository userRepository;
    private final S3StorageService s3StorageService;
    private final LLMService llmService;

    @PostMapping("/generate")
    public GenerateDesignResponse generate(@RequestBody GenerateDesignRequest req,
                                           OAuth2AuthenticationToken token) {
        log.info("Generate request received | prompt='{}'", req.getPrompt());

        var img = llmService.generateImage(
                new LLMService.ImageRequest(req.getPrompt()), null);

        User user = findUser(token);

        log.debug("Image created | bytes={}", img.imageBytes().length);

        Design design = designService.saveDesign(user, img.imageBytes());

        log.info("Design stored | user={}, key={}, url={}",
                user.getId(), design.getS3Key(), design.getUrl());

        return new GenerateDesignResponse(
                design.getS3Key(),
                design.getUrl()
        );
    }



    private User findUser(OAuth2AuthenticationToken token) {
        String googleId = token.getPrincipal().getAttribute("sub");
        return userRepository.findByGoogleId(googleId)
                .orElseThrow();
    }
}
