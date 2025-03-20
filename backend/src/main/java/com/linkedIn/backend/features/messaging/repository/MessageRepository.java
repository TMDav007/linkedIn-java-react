package com.linkedIn.backend.features.messaging.repository;

import com.linkedIn.backend.features.messaging.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
}
