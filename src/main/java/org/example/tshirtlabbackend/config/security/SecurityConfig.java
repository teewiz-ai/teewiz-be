package org.example.tshirtlabbackend.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@Profile("!dev")
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * The front-end origin, e.g. http://localhost:3000 or https://app.myapp.com
     * This is used to build a post-login redirect URL.
     */
    @Value("${app.frontend-origin}")
    private String frontendOrigin;

    private final CustomOAuth2UserService customOauth2UserService;

    /**
     * Expose a CorsFilter bean that uses the spring.web.cors configuration.
     * Alternatively, you can skip this if you only rely on the YAML‐defined CORS mappings,
     * but exposing a CorsFilter explicitly makes sure Spring Security picks it up.
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // “/api/**” matches what you already have in application.yml
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(frontendOrigin));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1.  Enable CORS based on the corsFilter bean
                .cors(Customizer.withDefaults())

                // 2.  CSRF: we want a cookie‐based CSRF token so that if you ever do a POST/PUT/DELETE
                //     from Next.js, you can read the CSRF token from a cookie. (See SessionAuthenticationStrategy below.)
//                .csrf(csrf -> csrf
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                )
                .csrf(AbstractHttpConfigurer::disable)


                // 3.  Session management: use stateful sessions (default). If you prefer JWT,
                //     swap out this line for a stateless session + token filter.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // 4.  Authorize endpoints.
                //     - Allow unauthenticated access to our OAuth2 endpoints & “/api/public/**”
                //     - Require authentication for anything under “/api/**” otherwise
                .authorizeHttpRequests(authz -> authz
                        // Public API & OAuth2 login endpoints
                        .requestMatchers("/oauth2/**", "/login/**", "/error").permitAll()

                        // If you want any public API, expose them here:
                        .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()

                        // Everything else under /api/** needs login
                        .requestMatchers("/api/**").authenticated()

                        // No UI on backend, so any other request can also be denied or authenticated.
                        .anyRequest().authenticated()
                )

                // 5.  OAuth2 login configuration
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google")
                        .defaultSuccessUrl(frontendOrigin, true)
                        .userInfoEndpoint(ui -> ui
                                .oidcUserService(customOauth2UserService)   // ⬅️ here
                        )
                )

                // 6.  Logout: invalidate session & clear cookies, then redirect to front end:
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // Remove session cookie(s), etc.
                            response.setStatus(204);
                        })
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}
