package com.atlas.postservice.controller;

import com.atlas.postservice.entity.Post;
import com.atlas.postservice.entity.PostComment;
import com.atlas.postservice.entity.PostLike;
import com.atlas.postservice.entity.PostShare;
import com.atlas.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody Post post) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(UUID.fromString(userId), post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID id,
            @RequestBody Post postDetails) {
        return ResponseEntity.ok(postService.updatePost(UUID.fromString(userId), id, postDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID id) {
        postService.deletePost(UUID.fromString(userId), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<PostLike> likePost(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(postService.likePost(UUID.fromString(userId), id));
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<PostComment> addComment(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID id,
            @RequestBody PostComment comment) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.addComment(UUID.fromString(userId), id, comment));
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<PostShare> sharePost(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.sharePost(UUID.fromString(userId), id));
    }
}
