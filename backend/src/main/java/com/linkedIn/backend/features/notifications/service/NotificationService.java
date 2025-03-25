package com.linkedIn.backend.features.notifications.service;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import com.linkedIn.backend.features.feed.model.Comment;
import com.linkedIn.backend.features.feed.model.Post;
import com.linkedIn.backend.features.messaging.model.Conversation;
import com.linkedIn.backend.features.messaging.model.Message;
import com.linkedIn.backend.features.networking.model.Connection;
import com.linkedIn.backend.features.networking.model.Status;
import com.linkedIn.backend.features.notifications.NotificationType;
import com.linkedIn.backend.features.notifications.model.Notification;
import com.linkedIn.backend.features.notifications.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;


    public NotificationService(NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public List<Notification> getUserNotifications(AuthenticationUser user) {
        return notificationRepository.findByRecipientOrderByCreationDateDesc(user);
    }

    public Notification markNotificationAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setRead(true);
        messagingTemplate.convertAndSend("/topic/users/" + notification.getRecipient().getId() + "/notifications",
                notification);
        return notificationRepository.save(notification);
    }

    public void sendDeleteNotificationToPost(UUID postId) {
        messagingTemplate.convertAndSend("/topic/posts/" + postId + "/delete", postId);
    }

    public void sendEditNotificationToPost(UUID postId, Post post) {
        messagingTemplate.convertAndSend("/topic/posts/" + postId + "/edit", post);
    }

    public void sendNewPostNotificationToFeed(Post post) {
        for (Connection connection : post.getAuthor().getInitiatedConnections()) {
            if (connection.getStatus().equals(Status.ACCEPTED)) {
                messagingTemplate.convertAndSend("/topic/feed/" + connection.getRecipient().getId() + "/post", post);
            }
        }
        for (Connection connection : post.getAuthor().getReceivedConnections()) {
            if (connection.getStatus().equals(Status.ACCEPTED)) {
                messagingTemplate.convertAndSend("/topic/feed/" + connection.getAuthor().getId() + "/post", post);
            }
        }
    }

    public void sendLikeToPost(UUID postId, Set<AuthenticationUser> likes) {
        messagingTemplate.convertAndSend("/topic/likes/" + postId, likes);
    }

    public void sendCommentToPost(UUID postId, Comment comment) {
        messagingTemplate.convertAndSend("/topic/comments/" + postId, comment);
    }

    public void sendDeleteCommentToPost(UUID postId, Comment comment) {
        messagingTemplate.convertAndSend("/topic/comments/" + postId + "/delete", comment);
    }

    public void sendLikeNotification(AuthenticationUser author, AuthenticationUser recipient, UUID resourceId) {
        if (author.getId().equals(recipient.getId())) {
            return;
        }

        Notification notification = new Notification(
                author,
                recipient,
                NotificationType.LIKE,
                resourceId);
        notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/users/" + recipient.getId() + "/notifications", notification);
    }

    public void sendCommentNotification(AuthenticationUser author, AuthenticationUser recipient, UUID resourceId) {
        if (author.getId().equals(recipient.getId())) {
            return;
        }

        Notification notification = new Notification(
                author,
                recipient,
                NotificationType.COMMENT,
                resourceId);
        notificationRepository.save(notification);

        messagingTemplate.convertAndSend("/topic/users/" + recipient.getId() + "/notifications", notification);
    }

    public void sendConversationToUsers(UUID senderId, UUID receiverId, Conversation conversation) {
        messagingTemplate.convertAndSend("/topic/users/" + senderId + "/conversations", conversation);
        messagingTemplate.convertAndSend("/topic/users/" + receiverId + "/conversations", conversation);
    }


    public void sendMessageToConversation(UUID conversationId, Message message) {
        messagingTemplate.convertAndSend("/topic/conversations/" + conversationId + "/messages", message);
    }

    public void sendNewInvitationToUsers(UUID senderId, UUID receiverId, Connection connection) {
        messagingTemplate.convertAndSend("/topic/users/" + senderId + "/connections/new", connection);
        messagingTemplate.convertAndSend("/topic/users/" + receiverId + "/connections/new", connection);
    }

    public void sendInvitationAcceptedToUsers(UUID senderId, UUID receiverId, Connection connection) {
        messagingTemplate.convertAndSend("/topic/users/" + senderId + "/connections/accepted", connection);
        messagingTemplate.convertAndSend("/topic/users/" + receiverId + "/connections/accepted", connection);
    }

    public void sendRemoveConnectionToUsers(UUID senderId, UUID receiverId, Connection connection) {
        messagingTemplate.convertAndSend("/topic/users/" + senderId + "/connections/remove", connection);
        messagingTemplate.convertAndSend("/topic/users/" + receiverId + "/connections/remove", connection);
    }

    public void sendConnectionSeenNotification(UUID id, Connection connection) {
        messagingTemplate.convertAndSend("/topic/users/" + id + "/connections/seen", connection);
    }

}
