package com.atlas.userservice.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationDto {
    private UUID id;
    private String institution;
    private String degree;
    private String field;
    private int startYear;
    private int endYear;
}
