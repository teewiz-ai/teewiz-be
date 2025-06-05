package org.example.tshirtlabbackend.design.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tshirtlabbackend.common.PaginatedResponse;
import org.example.tshirtlabbackend.common.PaginatedResult;
import org.example.tshirtlabbackend.design.controller.request.CreateDesignRequest;
import org.example.tshirtlabbackend.design.controller.request.SearchDesignRequest;
import org.example.tshirtlabbackend.design.controller.response.DesignResponse;
import org.example.tshirtlabbackend.design.mapper.DesignMapper;
import org.example.tshirtlabbackend.design.service.DesignService;
import org.example.tshirtlabbackend.design.service.command.CreateDesignCommand;
import org.example.tshirtlabbackend.design.service.command.SearchDesignCommand;
import org.example.tshirtlabbackend.design.service.result.CreateDesignResult;
import org.example.tshirtlabbackend.design.service.result.DesignResult;
import org.example.tshirtlabbackend.user.domain.User;
import org.example.tshirtlabbackend.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/designs")
@RequiredArgsConstructor
public class DesignController {

    private final DesignService designService;
    private final UserRepository userRepository;

    private User currentUser(Authentication auth) {
        if (auth instanceof OAuth2AuthenticationToken oauthToken) {
            String googleSub = oauthToken.getPrincipal().getAttribute("sub");
            return userRepository.findByGoogleSub(googleSub)
                    .orElseThrow(() -> new IllegalStateException("User not found for Google Sub: " + googleSub));
        }
        if (auth instanceof UsernamePasswordAuthenticationToken) {
            return (User) ((UsernamePasswordAuthenticationToken) auth).getPrincipal();
        }
        throw new IllegalStateException("Unsupported authentication type: " + auth.getClass().getSimpleName());
    }

    @PostMapping
    public ResponseEntity<DesignResponse> createDesign(
            @RequestBody @Validated CreateDesignRequest req,
            Authentication auth
    ) {

        User user = currentUser(auth);

        CreateDesignCommand cmd = DesignMapper.INSTANCE.toCommand(req, user);
        CreateDesignResult result = designService.createDesign(cmd);
        return ResponseEntity.ok(DesignMapper.INSTANCE.toDesignResponse(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DesignResponse> getDesignById(
            @PathVariable("id") Long id,
            Authentication auth
    ) {
        User user = currentUser(auth);
        DesignResult result = designService.getDesign(id, user);
        return ResponseEntity.ok(DesignMapper.INSTANCE.toResponse(result));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<DesignResponse>> listDesigns(
            @Validated SearchDesignRequest req,
            Authentication auth
    ) {

        User user = currentUser(auth);
        SearchDesignCommand cmd = DesignMapper.INSTANCE.toCommand(req, user);

        PaginatedResult<DesignResult> designs = designService.searchDesigns(cmd);

        return ResponseEntity.ok(DesignMapper.INSTANCE.toResponse(designs));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesign(
            @PathVariable("id") Long id,
            Authentication auth) {

        User user = currentUser(auth);
        designService.deleteDesign(id, user);

        return ResponseEntity.noContent().build();
    }
}