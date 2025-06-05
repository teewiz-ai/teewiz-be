package org.example.tshirtlabbackend.config.security;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tshirtlabbackend.user.domain.User;
import org.example.tshirtlabbackend.user.repository.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends OidcUserService {

    private final UserRepository users;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        String sub      = oidcUser.getSubject();
        String email    = oidcUser.getEmail();
        String name     = oidcUser.getFullName();
        String picture  = oidcUser.getPicture();

        User user = users.findByGoogleSub(sub)
                .orElseGet(() -> {
                    User u = new User();
                    u.setGoogleSub(sub);
                    return u;
                });

        user.setEmail(email);
        user.setName(name);
        user.setPictureUrl(picture);
        user.setLastLoginAt(Instant.now());
        users.save(user);

        return oidcUser;
    }
}
