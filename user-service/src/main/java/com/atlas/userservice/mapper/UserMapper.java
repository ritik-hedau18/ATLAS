package com.atlas.userservice.mapper;

import com.atlas.userservice.dto.*;
import com.atlas.userservice.entity.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public ProfileDto toProfileDto(User user) {
        if (user == null) {
            return null;
        }

        return ProfileDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .headline(user.getHeadline())
                .bio(user.getBio())
                .profilePhotoUrl(user.getProfilePhotoUrl())
                .bannerUrl(user.getBannerUrl())
                .location(user.getLocation())
                .website(user.getWebsite())
                .profileViews(user.getProfileViews())
                .profileCompletenessScore(calculateCompletenessScore(user))
                .createdAt(user.getCreatedAt())
                .experiences(user.getExperiences() != null ? user.getExperiences().stream().map(this::toExperienceDto).collect(Collectors.toList()) : Collections.emptyList())
                .educations(user.getEducations() != null ? user.getEducations().stream().map(this::toEducationDto).collect(Collectors.toList()) : Collections.emptyList())
                .skills(user.getSkills() != null ? user.getSkills().stream().map(this::toSkillDto).collect(Collectors.toList()) : Collections.emptyList())
                .certifications(user.getCertifications() != null ? user.getCertifications().stream().map(this::toCertificationDto).collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }

    public ExperienceDto toExperienceDto(Experience exp) {
        if (exp == null) return null;
        return ExperienceDto.builder()
                .id(exp.getId())
                .company(exp.getCompany())
                .title(exp.getTitle())
                .startDate(exp.getStartDate())
                .endDate(exp.getEndDate())
                .current(exp.isCurrent())
                .description(exp.getDescription())
                .build();
    }

    public EducationDto toEducationDto(Education edu) {
        if (edu == null) return null;
        return EducationDto.builder()
                .id(edu.getId())
                .institution(edu.getInstitution())
                .degree(edu.getDegree())
                .field(edu.getField())
                .startYear(edu.getStartYear())
                .endYear(edu.getEndYear())
                .build();
    }

    public SkillDto toSkillDto(Skill skill) {
        if (skill == null) return null;
        return SkillDto.builder()
                .id(skill.getId())
                .name(skill.getName())
                .endorsementCount(skill.getEndorsementCount())
                .build();
    }

    public CertificationDto toCertificationDto(Certification cert) {
        if (cert == null) return null;
        return CertificationDto.builder()
                .id(cert.getId())
                .name(cert.getName())
                .issuer(cert.getIssuer())
                .issueDate(cert.getIssueDate())
                .credentialUrl(cert.getCredentialUrl())
                .build();
    }

    private int calculateCompletenessScore(User user) {
        int score = 0;
        // Profile completeness score formula:
        // headline: 15%, experience: 25%, skills: 20%, bio: 15%, education: 15%, photo: 10%
        if (user.getHeadline() != null && !user.getHeadline().trim().isEmpty()) {
            score += 15;
        }
        if (user.getExperiences() != null && !user.getExperiences().isEmpty()) {
            score += 25;
        }
        if (user.getSkills() != null && !user.getSkills().isEmpty()) {
            score += 20;
        }
        if (user.getBio() != null && !user.getBio().trim().isEmpty()) {
            score += 15;
        }
        if (user.getEducations() != null && !user.getEducations().isEmpty()) {
            score += 15;
        }
        if (user.getProfilePhotoUrl() != null && !user.getProfilePhotoUrl().trim().isEmpty()) {
            score += 10;
        }
        return score;
    }
}
