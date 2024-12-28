package com.linkedIn.backend.features.feed.services;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import com.linkedIn.backend.features.authentication.repository.AuthenticaltionUserRepository;
import com.linkedIn.backend.features.feed.dto.PostDto;
import com.linkedIn.backend.features.feed.model.Comment;
import com.linkedIn.backend.features.feed.model.Post;
import com.linkedIn.backend.features.feed.repository.CommentRepository;
import com.linkedIn.backend.features.feed.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FeedService {

    private final PostRepository postRepository;
    private final AuthenticaltionUserRepository userRepository;
    private final CommentRepository commentRepository;


    public FeedService(PostRepository postRepository, AuthenticaltionUserRepository userRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public Post createPost(PostDto postDto, UUID authorId) {
        AuthenticationUser author = userRepository.findById(authorId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post post = new Post(postDto.getContent(), author);
        post.setPicture(postDto.getPicture());
        return postRepository.save(post);
    }

    public Post editPost(UUID postId, UUID authorId, PostDto postDto) {
        AuthenticationUser author = userRepository.findById(authorId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getAuthor().equals(author)) {
            throw new IllegalArgumentException("You are not allowed to edit this post");
        }
        post.setPicture(postDto.getPicture());
        post.setContent(postDto.getContent());

        return postRepository.save(post);
    }

    public List<Post> getFeedPosts(UUID authenticatedUserId) {
        return postRepository.findByAuthorIdNotOrderByCreationDateDesc(authenticatedUserId);
    }

    public List<Post> getAllPosts () {
        return postRepository.findAllByOrderByCreationDateDesc();
    }

    public void deletePost(UUID postId, UUID userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        AuthenticationUser author = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!post.getAuthor().equals(author)) {
            throw new IllegalArgumentException("You are not allowed to delete this post");
        }
        postRepository.delete(post);
    }

    public List<Post> getPostsByUserId(UUID userId) {
        return postRepository.findByAuthorId(userId);
    }

    public Post getPost(UUID postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }

    public Post likePost(UUID postId, UUID id) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        AuthenticationUser author = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(post.getLikes().contains(author)) {
            post.getLikes().remove(author);
        }else {
            post.getLikes().add(author);
        }

        return postRepository.save(post);

    }

    public Comment addComment(UUID postId, UUID userId, String content) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        AuthenticationUser user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Comment comment = new Comment(post, user, content);
        return commentRepository.save(comment);
    }

    public List<Comment> getPostComments(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return postRepository.findAllById(postId);
    }

    public Comment editComment(UUID commentId, UUID userId, String content) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        AuthenticationUser author = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!comment.getAuthor().equals(author)) {
            throw new IllegalArgumentException("You are not allowed to edit this comment");
        }

        comment.setContent(content);
        return commentRepository.save(comment);
    }

    public void deleteComment(UUID commentId, UUID id) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        AuthenticationUser author = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!comment.getAuthor().equals(author)) {
            throw new IllegalArgumentException("You are not allowed to delete this comment");
        }

        commentRepository.delete(comment);
    }
}
