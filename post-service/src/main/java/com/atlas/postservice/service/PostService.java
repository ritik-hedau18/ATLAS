package com.atlas.postservice.service;

import com.atlas.postservice.entity.Post;
import com.atlas.postservice.entity.PostComment;
import com.atlas.postservice.entity.PostLike;
import com.atlas.postservice.entity.PostShare;

import java.util.UUID;

public interface PostService {
    Post createPost(UUID authorId, Post post);
    Post updatePost(UUID authorId, UUID postId, Post postDetails);
    void deletePost(UUID authorId, UUID postId);
    Post getPostById(UUID postId);
    PostLike likePost(UUID userId, UUID postId);
    PostComment addComment(UUID userId, UUID postId, PostComment comment);
    PostShare sharePost(UUID userId, UUID postId);
}
