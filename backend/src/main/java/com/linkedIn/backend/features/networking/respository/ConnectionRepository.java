package com.linkedIn.backend.features.networking.respository;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import com.linkedIn.backend.features.networking.model.Connection;
import com.linkedIn.backend.features.networking.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ConnectionRepository extends JpaRepository<Connection, UUID> {
    boolean existsByAuthorAndRecipient(AuthenticationUser sender, AuthenticationUser recipient);

    List<Connection> findAllByAuthorOrRecipient(AuthenticationUser userOne, AuthenticationUser userTwo);

    @Query("SELECT c FROM connections c WHERE (c.author = :user OR c.recipient = :user) AND c.status = :status")
    List<Connection> findConnectionsByUserAndStatus(@Param("user") AuthenticationUser user, @Param("status") Status status);

    List<Connection> findByAuthorIdAndStatusOrRecipientIdAndStatus(UUID authenticatedUserId, Status status, UUID authenticatedUserId1, Status status1);
}
