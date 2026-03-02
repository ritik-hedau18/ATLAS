package com.atlas.userservice.service;

import com.atlas.userservice.dto.*;
import java.util.List;
import java.util.UUID;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(String refreshToken);
    ProfileDto getProfile(UUID userId, UUID viewerId);
    ProfileDto updateProfile(UUID userId, ProfileDto profileDto);
    ProfileDto addExperience(UUID userId, ExperienceDto experienceDto);
    ProfileDto addEducation(UUID userId, EducationDto educationDto);
    ProfileDto addSkill(UUID userId, SkillDto skillDto);
    List<ProfileViewResponse> getProfileViews(UUID userId);
}
