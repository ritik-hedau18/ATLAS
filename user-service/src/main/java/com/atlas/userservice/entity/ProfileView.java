package com.atlas.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "profile_views")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileView {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "viewer_id", nullable = false)
    private UUID viewerId;

    @Column(name = "viewed_id", nullable = false)
    private UUID viewedId;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    @PrePersist
    protected void onCreate() {
        this.viewedAt = LocalDateTime.now();
    }
}
