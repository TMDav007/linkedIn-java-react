package com.linkedIn.backend.features.networking.model;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name= "connections")
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id", updatable=false, nullable=false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private AuthenticationUser author;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private AuthenticationUser recipient;

    @NotNull
    private Status status = Status.PENDING;

    private Boolean seen = false;

    @CreationTimestamp
    private LocalDateTime connectionDate;

    public Connection() {
    }

    public Connection(AuthenticationUser author, AuthenticationUser recipient) {
        this.author = author;
        this.recipient = recipient;
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

    public @NotNull Status getStatus() {
        return status;
    }

    public void setStatus(@NotNull Status status) {
        this.status = status;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public LocalDateTime getConnectionDate() {
        return connectionDate;
    }

    public void setConnectionDate(LocalDateTime connectionDate) {
        this.connectionDate = connectionDate;
    }
}
