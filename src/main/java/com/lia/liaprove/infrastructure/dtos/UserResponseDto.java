package com.lia.liaprove.infrastructure.dtos;

import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRole;

import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String name,
        String email,
        String occupation,
        String bio,
        ExperienceLevel experienceLevel,
        UserRole role,
        String companyName,
        String companyEmail
) {}
