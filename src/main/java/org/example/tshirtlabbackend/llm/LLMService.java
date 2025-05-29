// src/main/java/org/example/tshirtlabbackend/llm/LLMService.java
package org.example.tshirtlabbackend.llm;

import org.example.tshirtlabbackend.design.domain.request.ImageGenRequest;
import org.example.tshirtlabbackend.design.domain.response.ImageGenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Service
public class LLMService {

    private final WebClient webClient;

    public LLMService(
            @Value("${llm.service.base-url}") String baseUrl,
            WebClient.Builder webClientBuilder
    ) {
        int maxSize = 10 * 1024 * 1024; // 10 MB

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxSize))
                .build();

        WebClient.Builder builder = webClientBuilder
                .baseUrl(baseUrl)
                .exchangeStrategies(strategies)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        this.webClient = builder.build();
    }

    /**
     * Calls POST {baseUrl}/images/generate
     * and returns the first image as raw bytes.
     */
    public byte[] generateImage(ImageGenRequest req) {
        ImageGenResponse resp = webClient.post()
                .uri("/images/generate")
                .bodyValue(req)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(err ->
                                        Mono.error(new RuntimeException("LLM service error: " + err))
                                )
                )
                .bodyToMono(ImageGenResponse.class)
                .block();

        if (resp == null || resp.getImages().isEmpty()) {
            throw new IllegalStateException("No image returned from LLM service");
        }

        String b64 = resp.getImages().get(0);
        return Base64.getDecoder().decode(b64);
    }
}
