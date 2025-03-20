package com.linkedIn.backend.features.messaging.model;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name="conversations")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", updatable=false, nullable=false)
    private UUID id;

    // many conversations to one author
    @ManyToOne(optional = false)
    private AuthenticationUser author;

    // many conversations to one recepient
    @ManyToOne(optional = false)
    private AuthenticationUser recipient;

    // one conversation to many messages
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    public Conversation() {

    }

    public Conversation(AuthenticationUser recipient, AuthenticationUser author) {
        this.recipient = recipient;
        this.author = author;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AuthenticationUser getAuthor() {
        return author;
    }

    public void setAuthor(AuthenticationUser author) {
        this.author = author;
    }

    public AuthenticationUser getRecipient() {
        return recipient;
    }

    public void setRecipient(AuthenticationUser recipient) {
        this.recipient = recipient;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
