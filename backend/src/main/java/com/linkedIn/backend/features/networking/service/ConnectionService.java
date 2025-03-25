package com.linkedIn.backend.features.networking.service;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import com.linkedIn.backend.features.authentication.repository.AuthenticaltionUserRepository;
import com.linkedIn.backend.features.networking.model.Connection;
import com.linkedIn.backend.features.networking.model.Status;
import com.linkedIn.backend.features.networking.respository.ConnectionRepository;
import com.linkedIn.backend.features.notifications.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ConnectionService {
    private final ConnectionRepository connectionRepository;
    private final AuthenticaltionUserRepository userRepository;
    private final NotificationService notificationService;

    public ConnectionService(ConnectionRepository connectionRepository, AuthenticaltionUserRepository userRepository, NotificationService notificationService) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public Connection sendConnectionRequest(AuthenticationUser sender, UUID recipientId) {
        AuthenticationUser recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

        if (connectionRepository.existsByAuthorAndRecipient(sender, recipient) ||
                connectionRepository.existsByAuthorAndRecipient(recipient, sender)) {
            throw new IllegalStateException("Connection request already exists");
        }

        Connection connection = connectionRepository.save(new Connection(sender, recipient));
        notificationService.sendNewInvitationToUsers(sender.getId(), recipient.getId(), connection);
        return connection;
    }

    public Connection acceptConnectionRequest(AuthenticationUser recipient, UUID connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));

        if (!connection.getRecipient().getId().equals(recipient.getId())) {
            throw new IllegalStateException("User is not the recipient of the connection request");
        }

        if (connection.getStatus().equals(Status.ACCEPTED)) {
            throw new IllegalStateException("Connection is already accepted");
        }

        connection.setStatus(Status.ACCEPTED);
        notificationService.sendInvitationAcceptedToUsers(connection.getAuthor().getId(), connection.getRecipient().getId(), connection);
        return connectionRepository.save(connection);
    }

    public Connection rejectOrCancelConnection(AuthenticationUser recipient, UUID connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));

        if (!connection.getRecipient().getId().equals(recipient.getId()) && !connection.getAuthor().getId().equals(recipient.getId())) {
            throw new IllegalStateException("User is not the recipient or author of the connection request");
        }
        connectionRepository.deleteById(connectionId);
        notificationService.sendRemoveConnectionToUsers(connection.getAuthor().getId(), connection.getRecipient().getId(), connection);
        return connection;
    }

    public List<Connection> getUserConnections(AuthenticationUser user, Status status) {
        return connectionRepository.findConnectionsByUserAndStatus(user, status != null ? status : Status.ACCEPTED);
    }

    public List<AuthenticationUser> getConnectionSuggestions(AuthenticationUser user) {
        List<AuthenticationUser> allUsers = userRepository.findAllByIdNot(user.getId());
        List<Connection> userConnections = connectionRepository.findAllByAuthorOrRecipient(user, user);

        Set<UUID> connectedUserIds = userConnections.stream()
                .flatMap(connection -> Stream.of(connection.getAuthor().getId(), connection.getRecipient().getId()))
                .collect(Collectors.toSet());

        return allUsers.stream()
                .filter(u -> !connectedUserIds.contains(u.getId()))
                .collect(Collectors.toList());

    }

    public Connection markConnectionAsSeen(AuthenticationUser user, UUID id) {
        Connection connection = connectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));

        if (!connection.getRecipient().getId().equals(user.getId())) {
            throw new IllegalStateException("User is not the recipient of the connection request");
        }

        connection.setSeen(true);
        notificationService.sendConnectionSeenNotification(connection.getRecipient().getId(), connection);
        return connectionRepository.save(connection);
    }
}
