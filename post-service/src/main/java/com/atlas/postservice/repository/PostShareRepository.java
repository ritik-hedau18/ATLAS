package com.atlas.postservice.repository;

import com.atlas.postservice.entity.PostShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostShareRepository extends JpaRepository<PostShare, UUID> {
    List<PostShare> findByOriginalPostId(UUID originalPostId);
}
