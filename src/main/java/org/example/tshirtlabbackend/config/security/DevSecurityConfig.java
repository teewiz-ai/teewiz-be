// src/main/java/.../security/DevSecurityConfig.java
package org.example.tshirtlabbackend.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;

@Configuration
@Profile("dev")
public class DevSecurityConfig {

    @Bean
    SecurityFilterChain devChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())                 // convenience
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .cors(Customizer.withDefaults());

        http.addFilterBefore((req, res, chain) -> {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                var principal   = new DefaultOAuth2User(
                        authorities, Map.of("sub", "dev-google-id"), "sub");
                var authToken   = new OAuth2AuthenticationToken(
                        principal, authorities, "google");
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            chain.doFilter(req, res);
        }, AnonymousAuthenticationFilter.class);

        return http.build();
    }
}
