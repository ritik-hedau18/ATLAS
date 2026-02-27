package com.atlas.postservice.repository;

import com.atlas.postservice.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    Page<Post> findByAuthorId(UUID authorId, Pageable pageable);
    List<Post> findByAuthorIdInOrderByCreatedAtDesc(List<UUID> authorIds);
}
