package org.example.tshirtlabbackend.user.domain;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public record UserDto(
        String name,
        String email
) {
    public static UserDto from(OAuth2AuthenticationToken token) {
        return new UserDto(
                token.getPrincipal().getAttribute("name"),
                token.getPrincipal().getAttribute("email")
        );
    }
}
