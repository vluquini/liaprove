package com.lia.liaprove.application.services.user;

import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRole;

import java.util.List;

/**
 * Record que possui os parâmetros obrigatórios para criação de um User.
 * Os demais parâmetros são definidos na implementação
 * da {@code UserFactory}.
 */
public record UserCreateDto(
        String name,
        String email,
        String passwordHash,
        String occupation,
        ExperienceLevel experienceLevel,
        UserRole role,
        List<String> hardSkills,
        List<String> softSkills,
        String companyName,
        String companyEmail
) {}
