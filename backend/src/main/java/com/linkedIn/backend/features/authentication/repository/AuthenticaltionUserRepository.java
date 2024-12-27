package com.linkedIn.backend.features.authentication.repository;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthenticaltionUserRepository  extends JpaRepository<AuthenticationUser, Long> {
    Optional<AuthenticationUser> findByEmail(String email);
}
