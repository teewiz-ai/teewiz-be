package org.example.tshirtlabbackend.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Exposes a singleton OpenAIClient so any @Component
 * (e.g. your OpenaiImageExecutor) can just @Autowire it.
 */
@Configuration
public class OpenAIConfig {

    @Bean
    public OpenAIClient openAIClient() {
        return OpenAIOkHttpClient.fromEnv();
    }
}