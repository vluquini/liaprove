package com.lia.liaprove.infrastructure.dtos.user;

import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.domain.user.UserStatus;

import java.util.UUID;

public record AuthenticatedUserResponse(
        UUID id,
        String name,
        String email,
        UserRole role,
        UserStatus status,
        String occupation,
        ExperienceLevel experienceLevel,
        String companyName,
        String companyEmail
) {
}
