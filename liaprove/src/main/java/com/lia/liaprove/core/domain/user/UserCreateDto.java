package com.lia.liaprove.core.domain.user;

public record UserCreateDto(
        String name,
        String email,
        String passwordHash,       // hash produzido pela infra (PasswordHasher)
        String occupation,
        ExperienceLevel experienceLevel,
        UserRole role
) {}
