package com.linkedIn.backend.features.feed.services;

import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import com.linkedIn.backend.features.authentication.repository.AuthenticaltionUserRepository;
import com.linkedIn.backend.features.feed.dto.PostDto;
import com.linkedIn.backend.features.feed.model.Comment;
import com.linkedIn.backend.features.feed.model.Post;
import com.linkedIn.backend.features.feed.repository.CommentRepository;
import com.linkedIn.backend.features.feed.repository.PostRepository;
import com.linkedIn.backend.features.networking.model.Connection;
import com.linkedIn.backend.features.networking.model.Status;
import com.linkedIn.backend.features.networking.respository.ConnectionRepository;
import com.linkedIn.backend.features.notifications.service.NotificationService;
import com.linkedIn.backend.features.storage.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeedService {

    private final PostRepository postRepository;
    private final AuthenticaltionUserRepository userRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;
    private final ConnectionRepository connectionRepository;
    private final StorageService storageService;


    public FeedService(PostRepository postRepository, AuthenticaltionUserRepository userRepository, CommentRepository commentRepository, NotificationService notificationService, ConnectionRepository connectionRepository, StorageService storageService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
        this.connectionRepository = connectionRepository;
        this.storageService = storageService;
    }

    public Post createPost(MultipartFile picture, String content, UUID id) throws Exception {
        AuthenticationUser author = userRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("User not found"));
//        Post post = new Post(postDto.getContent(), author);
//        post.setPicture(postDto.getPicture());

        String pictureUrl = storageService.saveImage(picture);
        Post post = new Post(content, author);
        post.setPicture(pictureUrl);
        post.setLikes(new HashSet<>());
        notificationService.sendNewPostNotificationToFeed(post);
        return postRepository.save(post);
    }

    public Post editPost(UUID postId, UUID id, MultipartFile picture, String content) throws Exception {
        AuthenticationUser author = userRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getAuthor().equals(author)) {
            throw new IllegalArgumentException("You are not allowed to edit this post");
        }

        String pictureUrl = storageService.saveImage(picture);
        post.setContent(content);
        post.setPicture(pictureUrl);

        notificationService.sendEditNotificationToPost(postId, post);
        return postRepository.save(post);
    }

    public List<Post> getFeedPosts(UUID authenticatedUserId) {
        List<Connection> connections = connectionRepository.findByAuthorIdAndStatusOrRecipientIdAndStatus(
                authenticatedUserId, Status.ACCEPTED, authenticatedUserId, Status.ACCEPTED
        );


        Set<UUID> connectedUserIds = connections.stream()
                .map(connection -> connection.getAuthor().getId().equals(authenticatedUserId)
                        ? connection.getRecipient().getId()
                        : connection.getAuthor().getId())
                .collect(Collectors.toSet());


        return postRepository.findByAuthorIdInOrderByCreationDateDesc((connectedUserIds));
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
        notificationService.sendDeleteNotificationToPost(postId);
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
            notificationService.sendLikeNotification(author, post.getAuthor(), postId);
        }

        Post savedPost = postRepository.save(post);
        notificationService.sendLikeToPost(postId, savedPost.getLikes());
        return savedPost;

    }

    public Comment addComment(UUID postId, UUID userId, String content) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        AuthenticationUser user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Comment comment = new Comment(post, user, content);
        Comment savedComment = commentRepository.save(comment);
        notificationService.sendCommentNotification(user, post.getAuthor(), postId);
        notificationService.sendCommentToPost(postId, savedComment);
        return savedComment;
    }

    public List<Comment> getPostComments(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return post.getComments();
    }

    public Set<AuthenticationUser> getPostLikes(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return post.getLikes();
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
