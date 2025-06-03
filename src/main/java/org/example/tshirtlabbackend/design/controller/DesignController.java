package org.example.tshirtlabbackend.design.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tshirtlabbackend.aws.S3StorageService;
import org.example.tshirtlabbackend.design.domain.Design;
import org.example.tshirtlabbackend.design.domain.request.GenerateDesignRequest;
import org.example.tshirtlabbackend.design.domain.request.ImageGenRequest;
import org.example.tshirtlabbackend.design.domain.response.GenerateDesignResponse;
import org.example.tshirtlabbackend.design.service.DesignService;
import org.example.tshirtlabbackend.llm.LLMService;
import org.example.tshirtlabbackend.user.domain.User;
import org.example.tshirtlabbackend.user.repository.UserRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;


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
    public GenerateDesignResponse generate(
            @RequestBody GenerateDesignRequest req,
            OAuth2AuthenticationToken token) {

        log.info("Generate request | prompt='{}', style='{}', size='{}', quality='{}', background='{}', format='{}', n={}, sampleImage={}",
                req.getPrompt(), req.getStyle(), req.getSize(), req.getQuality(),
                req.getBackground(), req.getFormat(), req.getN());


        byte[] imgResponse = llmService.generateImage(toImageGenRequest(req));

        User user = findUser(token);
        Design design = designService.saveDesign(user, imgResponse);

        log.info("Design stored | user={}, key={}, url={}",
                user.getId(), design.getS3Key(), design.getUrl());

        return new GenerateDesignResponse(design.getS3Key(), design.getUrl());
    }



    private User findUser(OAuth2AuthenticationToken token) {
        String googleId = token.getPrincipal().getAttribute("sub");
        return userRepository.findByGoogleId(googleId)
                .orElseThrow();
    }

    private ImageGenRequest toImageGenRequest(GenerateDesignRequest r) {
        return ImageGenRequest.builder()
                .prompt(r.getPrompt())
                .n(r.getN())
                .size(r.getSize())
                .quality(r.getQuality())
                .background(r.getBackground())
                .format(r.getFormat())
                .sampleImageUrls(r.getSampleImageUrls())
                .build();
    }
}
