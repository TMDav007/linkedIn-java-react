package com.linkedIn.backend.features.authentication.repository;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthenticaltionUserRepository  extends JpaRepository<AuthenticationUser, UUID> {
     Optional<AuthenticationUser> findByEmail(String email);

    List<AuthenticationUser> findAllByIdNot(UUID id);
}
