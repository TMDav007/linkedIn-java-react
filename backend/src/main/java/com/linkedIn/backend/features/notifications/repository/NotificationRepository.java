package com.linkedIn.backend.features.notifications.repository;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import com.linkedIn.backend.features.notifications.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByRecipient(AuthenticationUser recipient);

    List<Notification> findByRecipientOrderByCreationDateDesc(AuthenticationUser user);
}
