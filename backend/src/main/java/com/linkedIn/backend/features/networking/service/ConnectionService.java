package com.linkedIn.backend.features.networking.service;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import com.linkedIn.backend.features.authentication.repository.AuthenticaltionUserRepository;
import com.linkedIn.backend.features.networking.model.Connection;
import com.linkedIn.backend.features.networking.model.Status;
import com.linkedIn.backend.features.networking.respository.ConnectionRepository;
import com.linkedIn.backend.features.notifications.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public List<Connection> getUserConnections(UUID userId, Status status) {
      AuthenticationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
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

    public List<AuthenticationUser> getRecommendations(UUID userId, Integer limit) {
        AuthenticationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Set<AuthenticationUser> secondDegreeConnections = getSecondDegreeConnections(user);

        secondDegreeConnections = secondDegreeConnections.isEmpty() ? new HashSet<>(userRepository.findAllByIdNot(userId)) : secondDegreeConnections;

        List<UserRecommendation> recommendations = new ArrayList<>();

        for (AuthenticationUser potentialConnection : secondDegreeConnections) {
            if (!potentialConnection.getProfileComplete()) {
                continue;
            }

            double score = calculateProfileSimilarity(user, potentialConnection);

            int mutualConnections = countMutualConnections(user, potentialConnection);
            score += mutualConnections * 0.5;

            recommendations.add(new UserRecommendation(potentialConnection, score));
        }

        return recommendations.stream()
                .sorted((r1, r2) -> Double.compare(r2.score(), r1.score()))
                .limit(limit)
                .map(UserRecommendation::user)
                .collect(Collectors.toList());
    }

    private double calculateProfileSimilarity(AuthenticationUser user1, AuthenticationUser user2) {
        double score = 0.0;

        if (user1.getCompany().equalsIgnoreCase(user2.getCompany())) {
            score += 3.0;
        }
        if (user1.getPosition().equalsIgnoreCase(user2.getPosition())) {
            score += 2.0;
        }

        if (user1.getLocation().equalsIgnoreCase(user2.getLocation())) {
            score += 1.5;
        }

        return score;
    }

    private Set<AuthenticationUser> getSecondDegreeConnections(AuthenticationUser user) {
        Set<AuthenticationUser> directConnections = new HashSet<>();

        user.getInitiatedConnections().stream()
                .filter(conn -> conn.getStatus().equals(Status.ACCEPTED))
                .forEach(conn -> directConnections.add(conn.getRecipient()));

        user.getReceivedConnections().stream()
                .filter(conn -> conn.getStatus().equals(Status.ACCEPTED))
                .forEach(conn -> directConnections.add(conn.getAuthor()));

        Set<AuthenticationUser> secondDegreeConnections = new HashSet<>();

        for (AuthenticationUser directConnection : directConnections) {
            directConnection.getInitiatedConnections().stream()
                    .filter(conn -> conn.getStatus().equals(Status.ACCEPTED))
                    .forEach(conn -> secondDegreeConnections.add(conn.getRecipient()));

            directConnection.getReceivedConnections().stream()
                    .filter(conn -> conn.getStatus().equals(Status.ACCEPTED))
                    .forEach(conn -> secondDegreeConnections.add(conn.getAuthor()));
        }

        secondDegreeConnections.remove(user);
        secondDegreeConnections.removeAll(directConnections);

        return secondDegreeConnections;
    }

    private int countMutualConnections(AuthenticationUser user1, AuthenticationUser user2) {
        Set<AuthenticationUser> user1Connections = new HashSet<>();

        user1.getInitiatedConnections().stream()
                .filter(conn -> conn.getStatus().equals(Status.ACCEPTED))
                .forEach(conn -> user1Connections.add(conn.getRecipient()));
        user1.getReceivedConnections().stream()
                .filter(conn -> conn.getStatus().equals(Status.ACCEPTED))
                .forEach(conn -> user1Connections.add(conn.getAuthor()));

        Set<AuthenticationUser> user2Connections = new HashSet<>();
        user2.getInitiatedConnections().stream()
                .filter(conn -> conn.getStatus().equals(Status.ACCEPTED))
                .forEach(conn -> user2Connections.add(conn.getRecipient()));
        user2.getReceivedConnections().stream()
                .filter(conn -> conn.getStatus().equals(Status.ACCEPTED))
                .forEach(conn -> user2Connections.add(conn.getAuthor()));

        user1Connections.retainAll(user2Connections);
        return user1Connections.size();
    }

    private record UserRecommendation(AuthenticationUser user, double score) {
    }

}
