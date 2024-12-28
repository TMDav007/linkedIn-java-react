package com.linkedIn.backend.features.feed.controller;

import com.linkedIn.backend.dto.Response;
import com.linkedIn.backend.features.authentication.model.AuthenticationUser;
import com.linkedIn.backend.features.feed.dto.CommentDto;
import com.linkedIn.backend.features.feed.dto.PostDto;
import com.linkedIn.backend.features.feed.model.Comment;
import com.linkedIn.backend.features.feed.model.Post;
import com.linkedIn.backend.features.feed.services.FeedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {
    private final FeedService feedService;


    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping
    public ResponseEntity<List<Post>> getFeedPosts(
            @RequestAttribute("authenticatedUser") AuthenticationUser user
    ) {
        List<Post> posts = feedService.getFeedPosts(user.getId());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts(
    ) {
        List<Post> posts = feedService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestBody PostDto postDto,
                                           @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        Post post = feedService.createPost(postDto, user.getId());
        return ResponseEntity.ok(post);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Post> editPost(@PathVariable UUID postId,
                                         @RequestBody PostDto postDto,
                                         @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        Post post = feedService.editPost(postId, user.getId(), postDto);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable UUID postId) {
        Post post = feedService.getPost(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/posts/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable UUID userId) {
        List<Post> posts = feedService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }


    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Response> deletePost(@PathVariable UUID postId,
                                               @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        feedService.deletePost(postId, user.getId());
        return ResponseEntity.ok(new Response("Post deleted successfully."));
    }

    @PutMapping("/posts/{postId}/like")
    public ResponseEntity<Post> likePost(@PathVariable UUID postId, @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        Post post = feedService.likePost(postId, user.getId());
        return ResponseEntity.ok(post);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable UUID postId, @RequestBody CommentDto commentDto,
                                              @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        Comment comment = feedService.addComment(postId, user.getId(), commentDto.getContent());
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable UUID postId) {
        List<Comment> comments = feedService.getPostComments(postId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Response> deleteComment(@PathVariable UUID commentId,
                                                  @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        feedService.deleteComment(commentId, user.getId());
        return ResponseEntity.ok(new Response("Comment deleted successfully."));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Comment> editComment(@PathVariable UUID commentId, @RequestBody CommentDto commentDto,
                                               @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        Comment comment = feedService.editComment(commentId, user.getId(), commentDto.getContent());
        return ResponseEntity.ok(comment);
    }

}
