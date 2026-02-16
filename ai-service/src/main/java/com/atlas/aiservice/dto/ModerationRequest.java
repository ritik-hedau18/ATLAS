package com.atlas.aiservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModerationRequest {
    private String content;
}
