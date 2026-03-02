package com.atlas.userservice.repository;

import com.atlas.userservice.entity.ProfileView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProfileViewRepository extends JpaRepository<ProfileView, UUID> {
    List<ProfileView> findByViewedIdOrderByViewedAtDesc(UUID viewedId);
    long countByViewedId(UUID viewedId);
}
