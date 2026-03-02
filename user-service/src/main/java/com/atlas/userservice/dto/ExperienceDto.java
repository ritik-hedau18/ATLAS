package com.atlas.userservice.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceDto {
    private UUID id;
    private String company;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean current;
    private String description;
}
