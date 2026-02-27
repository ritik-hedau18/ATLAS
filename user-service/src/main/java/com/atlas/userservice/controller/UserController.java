package com.atlas.userservice.controller;

import com.atlas.userservice.dto.*;
import com.atlas.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}/profile")
    public ResponseEntity<ProfileDto> getProfile(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = false) String requesterId) {
        UUID viewerId = (requesterId != null) ? UUID.fromString(requesterId) : null;
        return ResponseEntity.ok(userService.getProfile(id, viewerId));
    }

    @PutMapping("/profile")
    public ResponseEntity<ProfileDto> updateProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody ProfileDto profileDto) {
        return ResponseEntity.ok(userService.updateProfile(UUID.fromString(userId), profileDto));
    }

    @PostMapping("/experience")
    public ResponseEntity<ProfileDto> addExperience(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody ExperienceDto experienceDto) {
        return ResponseEntity.ok(userService.addExperience(UUID.fromString(userId), experienceDto));
    }

    @PostMapping("/education")
    public ResponseEntity<ProfileDto> addEducation(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody EducationDto educationDto) {
        return ResponseEntity.ok(userService.addEducation(UUID.fromString(userId), educationDto));
    }

    @PostMapping("/skills")
    public ResponseEntity<ProfileDto> addSkill(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody SkillDto skillDto) {
        return ResponseEntity.ok(userService.addSkill(UUID.fromString(userId), skillDto));
    }

    @GetMapping("/{id}/profile-views")
    public ResponseEntity<List<ProfileViewResponse>> getProfileViews(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String requesterId) {
        if (!id.toString().equals(requesterId)) {
            throw new RuntimeException("Unauthorized access to profile views");
        }
        return ResponseEntity.ok(userService.getProfileViews(id));
    }
}
