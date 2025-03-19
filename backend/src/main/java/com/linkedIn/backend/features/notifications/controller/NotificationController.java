package com.linkedIn.backend.features.notifications.controller;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import com.linkedIn.backend.features.notifications.model.Notification;
import com.linkedIn.backend.features.notifications.repository.NotificationRepository;
import com.linkedIn.backend.features.notifications.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<Notification> getUserNotifications(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        return notificationService.getUserNotifications(user);
    }

    @PutMapping("/{notificationId}")
    public Notification markNotificationAsRead(@PathVariable UUID notificationId) {
        return notificationService.markNotificationAsRead(notificationId);
    }
}
