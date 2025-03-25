package com.linkedIn.backend.features.feed.repository;

import com.linkedIn.backend.features.feed.model.Comment;
import com.linkedIn.backend.features.feed.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findAllByOrderByCreationDateDesc();
    List<Post> findByAuthorId(UUID authorId);

    List<Comment> findAllById(UUID postId);

    List<Post> findByAuthorIdInOrderByCreationDateDesc(Set<UUID> connectedUserIds);
}
