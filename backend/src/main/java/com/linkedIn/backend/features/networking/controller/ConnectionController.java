package com.linkedIn.backend.features.networking.controller;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import com.linkedIn.backend.features.networking.model.Connection;
import com.linkedIn.backend.features.networking.model.Status;
import com.linkedIn.backend.features.networking.service.ConnectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/networking")
public class ConnectionController {
    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @GetMapping("/connections")
    public List<Connection> getUserConnections(@RequestAttribute("authenticatedUser") AuthenticationUser user, @RequestParam(name="status",required = false) Status status, @RequestParam(name="userId",required = false) UUID userId) {
       if (userId != null) {
           return connectionService.getUserConnections(userId, status);
       }
        return connectionService.getUserConnections(user, status);
    }


    @PostMapping("/connections")
    public Connection sendConnectionRequest(@RequestAttribute("authenticatedUser") AuthenticationUser sender, @RequestParam("recipientId")  UUID recipientId) {
        return connectionService.sendConnectionRequest(sender, recipientId);
    }

    @PutMapping("/connections/{id}")
    public Connection acceptConnectionRequest(@RequestAttribute("authenticatedUser") AuthenticationUser recipient, @PathVariable("id") UUID id) {
        return connectionService.acceptConnectionRequest(recipient, id);
    }

    @DeleteMapping("/connections/{id}")
    public Connection rejectOrCancelConnection(@RequestAttribute("authenticatedUser") AuthenticationUser recipient, @PathVariable("id")  UUID id) {
        return connectionService.rejectOrCancelConnection(recipient, id);
    }

    @PutMapping("/connections/{id}/seen")
    public Connection markConnectionAsSeen(@RequestAttribute("authenticatedUser") AuthenticationUser user, @PathVariable("id")  UUID id) {
        return connectionService.markConnectionAsSeen(user, id);
    }

    @GetMapping("/suggestions")
    public List<AuthenticationUser> getConnectionSuggestions(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        return connectionService.getConnectionSuggestions(user);
    }
}
