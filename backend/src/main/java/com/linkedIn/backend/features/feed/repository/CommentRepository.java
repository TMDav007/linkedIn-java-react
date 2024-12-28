package com.linkedIn.backend.features.feed.repository;

import com.linkedIn.backend.features.feed.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

}
