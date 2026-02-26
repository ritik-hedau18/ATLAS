package com.atlas.postservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModerationResponse {
    private boolean approved;
    private double toxicityScore;
    private String reason;
}
