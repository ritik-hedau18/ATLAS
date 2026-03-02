package com.atlas.userservice.service;

import com.atlas.userservice.dto.*;
import com.atlas.userservice.entity.*;
import com.atlas.userservice.kafka.UserEvent;
import com.atlas.userservice.kafka.UserEventProducer;
import com.atlas.userservice.mapper.UserMapper;
import com.atlas.userservice.repository.*;
import com.atlas.userservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProfileViewRepository profileViewRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final UserEventProducer userEventProducer;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .profileViews(0)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getId());

        // Publish event to Kafka
        publishUserEvent(user, "USER_CREATED");

        String accessToken = jwtService.generateToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        log.info("User logged in successfully: {}", user.getId());

        String accessToken = jwtService.generateToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        String userIdStr = jwtService.extractUsername(refreshToken);
        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, userIdStr)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String accessToken = jwtService.generateToken(user.getId(), user.getEmail());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }

    @Override
    @Transactional
    public ProfileDto getProfile(UUID userId, UUID viewerId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (viewerId != null && !viewerId.equals(userId)) {
            // Track view
            ProfileView view = ProfileView.builder()
                    .viewerId(viewerId)
                    .viewedId(userId)
                    .build();
            profileViewRepository.save(view);

            // Increment count
            user.setProfileViews(user.getProfileViews() + 1);
            userRepository.save(user);
        }

        return userMapper.toProfileDto(user);
    }

    @Override
    @Transactional
    public ProfileDto updateProfile(UUID userId, ProfileDto profileDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(profileDto.getFullName());
        user.setHeadline(profileDto.getHeadline());
        user.setBio(profileDto.getBio());
        user.setProfilePhotoUrl(profileDto.getProfilePhotoUrl());
        user.setBannerUrl(profileDto.getBannerUrl());
        user.setLocation(profileDto.getLocation());
        user.setWebsite(profileDto.getWebsite());

        user = userRepository.save(user);
        log.info("User profile updated: {}", user.getId());

        // Publish update event
        publishUserEvent(user, "USER_UPDATED");

        return userMapper.toProfileDto(user);
    }

    @Override
    @Transactional
    public ProfileDto addExperience(UUID userId, ExperienceDto experienceDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Experience exp = Experience.builder()
                .user(user)
                .company(experienceDto.getCompany())
                .title(experienceDto.getTitle())
                .startDate(experienceDto.getStartDate())
                .endDate(experienceDto.getEndDate())
                .current(experienceDto.isCurrent())
                .description(experienceDto.getDescription())
                .build();

        user.getExperiences().add(exp);
        userRepository.save(user);

        // Publish event
        publishUserEvent(user, "USER_UPDATED");

        return userMapper.toProfileDto(user);
    }

    @Override
    @Transactional
    public ProfileDto addEducation(UUID userId, EducationDto educationDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Education edu = Education.builder()
                .user(user)
                .institution(educationDto.getInstitution())
                .degree(educationDto.getDegree())
                .field(educationDto.getField())
                .startYear(educationDto.getStartYear())
                .endYear(educationDto.getEndYear())
                .build();

        user.getEducations().add(edu);
        userRepository.save(user);

        // Publish event
        publishUserEvent(user, "USER_UPDATED");

        return userMapper.toProfileDto(user);
    }

    @Override
    @Transactional
    public ProfileDto addSkill(UUID userId, SkillDto skillDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Skill skill = Skill.builder()
                .user(user)
                .name(skillDto.getName())
                .endorsementCount(0)
                .build();

        user.getSkills().add(skill);
        userRepository.save(user);

        // Publish event
        publishUserEvent(user, "USER_UPDATED");

        return userMapper.toProfileDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileViewResponse> getProfileViews(UUID userId) {
        List<ProfileView> views = profileViewRepository.findByViewedIdOrderByViewedAtDesc(userId);

        return views.stream().map(view -> {
            String name = "Anonymous User";
            String headline = "LinkedIn Member";
            String photo = "";
            try {
                User viewer = userRepository.findById(view.getViewerId()).orElse(null);
                if (viewer != null) {
                    name = viewer.getFullName();
                    headline = viewer.getHeadline();
                    photo = viewer.getProfilePhotoUrl();
                }
            } catch (Exception ignored) {}

            return ProfileViewResponse.builder()
                    .id(view.getId())
                    .viewerId(view.getViewerId())
                    .viewerName(name)
                    .viewerHeadline(headline)
                    .viewerPhotoUrl(photo)
                    .viewedAt(view.getViewedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    private void publishUserEvent(User user, String type) {
        try {
            UserEvent event = UserEvent.builder()
                    .eventId(UUID.randomUUID())
                    .eventType(type)
                    .userId(user.getId())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .headline(user.getHeadline())
                    .bio(user.getBio())
                    .location(user.getLocation())
                    .timestamp(LocalDateTime.now())
                    .build();
            userEventProducer.sendUserEvent(event);
        } catch (Exception e) {
            log.error("Failed to send Kafka event for user {}: {}", user.getId(), e.getMessage());
        }
    }
}
