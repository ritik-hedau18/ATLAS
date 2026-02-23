package com.atlas.feedservice.feign;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectionUserDto {
    private String userId;
    private String username;
    private String headline;
}
