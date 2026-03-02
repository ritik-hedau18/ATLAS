package com.atlas.userservice.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileViewResponse {
    private UUID id;
    private UUID viewerId;
    private String viewerName;
    private String viewerHeadline;
    private String viewerPhotoUrl;
    private LocalDateTime viewedAt;
}
