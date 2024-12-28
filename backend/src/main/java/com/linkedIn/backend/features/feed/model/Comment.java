package com.linkedIn.backend.features.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore
    private Post post;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private AuthenticationUser author;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    private LocalDateTime creationDate;

    private LocalDateTime updatedDate;

    public Comment() {
    }

    public Comment(Post post, AuthenticationUser author, String content) {
        this.content = content;
        this.author = author;
        this.post = post;
    }



    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public AuthenticationUser getAuthor() {
        return author;
    }

    public void setAuthor(AuthenticationUser author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
