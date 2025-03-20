package com.linkedIn.backend.features.messaging.controller;

import com.linkedIn.backend.dto.Response;
import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import com.linkedIn.backend.features.messaging.dto.MessageDto;
import com.linkedIn.backend.features.messaging.model.Conversation;
import com.linkedIn.backend.features.messaging.model.Message;
import com.linkedIn.backend.features.messaging.service.MessagingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messaging")
public class MessagingController {
    private final MessagingService messagingService;

    public MessagingController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @GetMapping("/conversations")
    public List<Conversation> getConversations(@RequestAttribute("authenticatedUser") AuthenticationUser user) {
        return messagingService.getConversationsOfUser(user);
    }

    @GetMapping("/conversations/{conversationId}")
    public Conversation getConversation(@RequestAttribute("authenticatedUser") AuthenticationUser user, @PathVariable UUID conversationId) {
        return messagingService.getConversation(user, conversationId);
    }

    @PostMapping("/conversations")
    public Conversation createConversationAndAddMessage(@RequestAttribute("authenticatedUser") AuthenticationUser sender, @RequestBody MessageDto messageDto) {
        return messagingService.createConversationAndAddMessage(sender, messageDto.receiverId(), messageDto.content());
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public Message addMessageToConversation(@RequestAttribute("authenticatedUser") AuthenticationUser sender, @RequestBody MessageDto messageDto, @PathVariable UUID conversationId) {
        return messagingService.addMessageToConversation(conversationId, sender, messageDto.receiverId(), messageDto.content());
    }

    @PutMapping("/conversations/messages/{messageId}")
    public Response markMessageAsRead(@RequestAttribute("authenticatedUser") AuthenticationUser user, @PathVariable UUID messageId) {
        messagingService.markMessageAsRead(user, messageId);
        return new Response("Message marked as read");
    }
}
