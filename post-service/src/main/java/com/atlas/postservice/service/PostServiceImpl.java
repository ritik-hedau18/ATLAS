package com.atlas.postservice.service;

import com.atlas.postservice.dto.ModerationRequest;
import com.atlas.postservice.dto.ModerationResponse;
import com.atlas.postservice.entity.Post;
import com.atlas.postservice.entity.PostComment;
import com.atlas.postservice.entity.PostLike;
import com.atlas.postservice.entity.PostShare;
import com.atlas.postservice.feign.AiServiceClient;
import com.atlas.postservice.kafka.PostEvent;
import com.atlas.postservice.kafka.PostEventProducer;
import com.atlas.postservice.repository.PostCommentRepository;
import com.atlas.postservice.repository.PostLikeRepository;
import com.atlas.postservice.repository.PostRepository;
import com.atlas.postservice.repository.PostShareRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostShareRepository postShareRepository;
    private final AiServiceClient aiServiceClient;
    private final PostEventProducer postEventProducer;

    private void moderateContent(String content) {
        log.info("Requesting moderation check for content: {}", content.substring(0, Math.min(content.length(), 20)));
        try {
            ModerationResponse response = aiServiceClient.moderate(new ModerationRequest(content));
            if (!response.isApproved() || response.getToxicityScore() > 0.85) {
                log.warn("Content rejected by moderation. Score: {}, Reason: {}", response.getToxicityScore(), response.getReason());
                throw new IllegalArgumentException("Content rejected due to toxicity: " + response.getReason());
            } else if (response.getToxicityScore() >= 0.6) {
                log.warn("Content flagged for review. Score: {}, Reason: {}", response.getToxicityScore(), response.getReason());
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI service moderation failed, applying fallback: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public Post createPost(UUID authorId, Post post) {
        // Moderate post content before saving
        moderateContent(post.getContent());

        post.setAuthorId(authorId);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setShareCount(0);

        Post savedPost = postRepository.save(post);
        log.info("Post created successfully: {}", savedPost.getId());

        // Publish event to Kafka
        postEventProducer.sendPostEvent(PostEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("POST_CREATED")
                .authorId(authorId)
                .postId(savedPost.getId())
                .content(savedPost.getContent())
                .visibility(savedPost.getVisibility())
                .timestamp(LocalDateTime.now())
                .build());

        return savedPost;
    }

    @Override
    @Transactional
    public Post updatePost(UUID authorId, UUID postId, Post postDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthorId().equals(authorId)) {
            throw new RuntimeException("Unauthorized edit request");
        }

        // Moderate new content
        moderateContent(postDetails.getContent());

        post.setContent(postDetails.getContent());
        post.setImageUrl(postDetails.getImageUrl());
        post.setVisibility(postDetails.getVisibility());

        Post updatedPost = postRepository.save(post);
        log.info("Post updated: {}", updatedPost.getId());

        // Publish event
        postEventProducer.sendPostEvent(PostEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("POST_UPDATED")
                .authorId(authorId)
                .postId(updatedPost.getId())
                .content(updatedPost.getContent())
                .visibility(updatedPost.getVisibility())
                .timestamp(LocalDateTime.now())
                .build());

        return updatedPost;
    }

    @Override
    @Transactional
    public void deletePost(UUID authorId, UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getAuthorId().equals(authorId)) {
            throw new RuntimeException("Unauthorized delete request");
        }

        postRepository.delete(post);
        log.info("Post deleted: {}", postId);

        // Publish event
        postEventProducer.sendPostEvent(PostEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("POST_DELETED")
                .authorId(authorId)
                .postId(postId)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Post getPostById(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Override
    @Transactional
    public PostLike likePost(UUID userId, UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostLike like = postLikeRepository.findByPostIdAndUserId(postId, userId).orElse(null);
        if (like != null) {
            // Unlike post
            postLikeRepository.delete(like);
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postRepository.save(post);
            log.info("Post unliked by user {}: {}", userId, postId);

            postEventProducer.sendPostEvent(PostEvent.builder()
                    .eventId(UUID.randomUUID())
                    .eventType("POST_UNLIKED")
                    .authorId(userId)
                    .postId(postId)
                    .timestamp(LocalDateTime.now())
                    .build());
            return null;
        } else {
            // Like post
            PostLike newLike = PostLike.builder()
                    .postId(postId)
                    .userId(userId)
                    .build();
            newLike = postLikeRepository.save(newLike);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
            log.info("Post liked by user {}: {}", userId, postId);

            postEventProducer.sendPostEvent(PostEvent.builder()
                    .eventId(UUID.randomUUID())
                    .eventType("POST_LIKED")
                    .authorId(userId)
                    .postId(postId)
                    .timestamp(LocalDateTime.now())
                    .build());
            return newLike;
        }
    }

    @Override
    @Transactional
    public PostComment addComment(UUID userId, UUID postId, PostComment comment) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Moderate comment content
        moderateContent(comment.getContent());

        comment.setPostId(postId);
        comment.setAuthorId(userId);

        PostComment savedComment = postCommentRepository.save(comment);
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        log.info("Comment added to post {}: {}", postId, savedComment.getId());

        // Publish event
        postEventProducer.sendPostEvent(PostEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("POST_COMMENTED")
                .authorId(userId)
                .postId(postId)
                .content(savedComment.getContent())
                .timestamp(LocalDateTime.now())
                .build());

        return savedComment;
    }

    @Override
    @Transactional
    public PostShare sharePost(UUID userId, UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostShare share = PostShare.builder()
                .originalPostId(postId)
                .sharedBy(userId)
                .build();

        share = postShareRepository.save(share);
        post.setShareCount(post.getShareCount() + 1);
        postRepository.save(post);
        log.info("Post {} shared by user {}", postId, userId);

        // Publish event
        postEventProducer.sendPostEvent(PostEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("POST_SHARED")
                .authorId(userId)
                .postId(postId)
                .timestamp(LocalDateTime.now())
                .build());

        return share;
    }
}
