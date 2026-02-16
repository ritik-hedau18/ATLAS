package com.atlas.aiservice.feign;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDto {
    private UUID id;
    private String title;
    private List<String> skillsRequired;
}
