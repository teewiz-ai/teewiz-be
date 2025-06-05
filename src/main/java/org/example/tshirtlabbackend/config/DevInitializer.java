package org.example.tshirtlabbackend.config;

import lombok.RequiredArgsConstructor;
import org.example.tshirtlabbackend.user.domain.User;
import org.example.tshirtlabbackend.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        userRepository.findByGoogleSub("dev-google-sub").orElseGet(() -> {
            User user = new User();
            user.setGoogleSub("dev-google-sub");
            user.setEmail("dev@example.com");
            user.setName("Dev User");
            return userRepository.save(user);
        });
    }
}
