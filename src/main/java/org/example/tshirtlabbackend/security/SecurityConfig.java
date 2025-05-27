package org.example.tshirtlabbackend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
class SecurityConfig {

    // inject appBaseUrl
    @Value("${app.frontend-origin}")
    private String appBaseUrl;

    @Bean
    AuthenticationSuccessHandler spaSuccessHandler() {
        // `redirect` query param wins; if absent, fall back to the root of the SPA
        return (request, response, authentication) -> {
            String redirect = request.getParameter("redirect");
            if (redirect == null || redirect.isBlank()) redirect = "/";
            String target = "http://localhost:3000" + redirect;   // ⚠️ front-end origin
            response.sendRedirect(target);
        };
    }


    @Bean
    SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .oauth2Login(o -> o.successHandler(spaSuccessHandler()))   // <-- here!
                .logout(l -> l.logoutSuccessUrl("http://localhost:3000/"))
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .cors(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    CorsConfigurationSource cors() {
        var cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:3000"));
        cfg.setAllowedMethods(List.of("*"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        var src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }
}
