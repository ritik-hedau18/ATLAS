package com.atlas.userservice.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDto {
    private UUID id;
    private String fullName;
    private String email;
    private String headline;
    private String bio;
    private String profilePhotoUrl;
    private String bannerUrl;
    private String location;
    private String website;
    private int profileViews;
    private int profileCompletenessScore;
    private LocalDateTime createdAt;
    private List<ExperienceDto> experiences;
    private List<EducationDto> educations;
    private List<SkillDto> skills;
    private List<CertificationDto> certifications;
}
