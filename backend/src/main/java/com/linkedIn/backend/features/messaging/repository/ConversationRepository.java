package com.linkedIn.backend.features.messaging.repository;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import com.linkedIn.backend.features.messaging.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    Optional<Conversation> findByAuthorAndRecipient(AuthenticationUser author, AuthenticationUser recipient);

    List<Conversation> findByAuthorOrRecipient(AuthenticationUser userOne, AuthenticationUser userTwo);
}
