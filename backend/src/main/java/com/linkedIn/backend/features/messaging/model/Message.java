package com.linkedIn.backend.features.messaging.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name="messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", updatable=false, nullable=false)
    private UUID id;

    @ManyToOne(optional = false)
    private AuthenticationUser sender;

    @ManyToOne(optional = false)
    private AuthenticationUser receiver;

    @JsonIgnore
    @ManyToOne(optional = false)
    private Conversation conversation;

    private String content;
    private Boolean isRead;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Message() {
    }

    public Message(AuthenticationUser sender, AuthenticationUser receiver, Conversation conversation, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.conversation = conversation;
        this.content = content;
        this.isRead = false;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AuthenticationUser getSender() {
        return sender;
    }

    public void setSender(AuthenticationUser sender) {
        this.sender = sender;
    }

    public AuthenticationUser getReceiver() {
        return receiver;
    }

    public void setReceiver(AuthenticationUser receiver) {
        this.receiver = receiver;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
