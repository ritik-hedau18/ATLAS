package com.atlas.feedservice.kafka;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedEntryEvent implements Serializable {
    private String userId;
    private String postId;
    private double score;
}
