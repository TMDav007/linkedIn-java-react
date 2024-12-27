package com.linkedIn.backend.configuration;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import com.linkedIn.backend.features.authentication.repository.AuthenticaltionUserRepository;
import com.linkedIn.backend.features.authentication.utils.Encoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabaseConfiguration {

    private final Encoder encoder;

    public LoadDatabaseConfiguration(Encoder encoder) {
        this.encoder = encoder;
    }

    @Bean
    CommandLineRunner initDatabase (AuthenticaltionUserRepository authenticaltionUserRepository) {
        return args -> {
            AuthenticationUser authenticationUser = new AuthenticationUser("yemiafolz@example.com", encoder.encode("password"));
            authenticaltionUserRepository.save(authenticationUser);
        };
    }
}
