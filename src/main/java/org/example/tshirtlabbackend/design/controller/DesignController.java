package org.example.tshirtlabbackend.design.controller;

import lombok.RequiredArgsConstructor;
import org.example.tshirtlabbackend.aws.S3StorageService;
import org.example.tshirtlabbackend.design.domain.Design;
import org.example.tshirtlabbackend.design.domain.DesignDto;
import org.example.tshirtlabbackend.design.domain.SaveDesignRequest;
import org.example.tshirtlabbackend.design.repository.DesignRepository;
import org.example.tshirtlabbackend.design.service.DesignService;
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

@RestController
@RequestMapping("/api/designs")
@RequiredArgsConstructor
public class DesignController {

    private final DesignService designService;
    private final UserRepository userRepository;
    private final S3StorageService s3StorageService;

    /** GET /api/designs — list current user designs */
    @GetMapping
    public List<DesignDto> list(OAuth2AuthenticationToken token) {
        User user = findUser(token);
        return designService.listDesigns(user)
                .stream()
                .map(DesignDto::from)
                .toList();
    }

    /** POST /api/designs {"s3Key": "designs/abc123.png"} */
    @PostMapping
    public DesignDto save(@RequestBody SaveDesignRequest request,
                          OAuth2AuthenticationToken token) {
        User user = findUser(token);
        Design design = designService.saveDesign(user, request.s3Key());
        return DesignDto.from(design);
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DesignDto upload(@RequestPart("file") MultipartFile file,
                            OAuth2AuthenticationToken token) {
        User user = findUser(token);
        Design d = designService.saveDesign(user, file);
        return DesignDto.from(d);
    }

    /**
     * GET /api/designs/{id}/file
     * Streams the binary data through Spring, for clients that can’t handle presigned URLs.
     */
    @GetMapping("/{id}/file")
    public ResponseEntity<StreamingResponseBody> download(@PathVariable Long id) {
        Design d = designService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        S3StorageService.GetObjectResponseWithStream obj = s3StorageService.download(d.getS3Key());

        StreamingResponseBody body = outputStream -> obj.stream().transferTo(outputStream);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(obj.metadata().contentType()))
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(obj.metadata().contentLength()))
                .body(body);
    }

    private User findUser(OAuth2AuthenticationToken token) {
        String googleId = token.getPrincipal().getAttribute("sub");
        return userRepository.findByGoogleId(googleId)
                .orElseThrow();
    }
}
