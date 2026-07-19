package com.lia.liaprove.infrastructure.dtos.user;

import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRole;

import java.util.List;
import java.util.UUID;

/**
 * DTO público que expõe apenas dados não sensíveis de um usuário.
 *
 * <p>Utilizado em endpoints acessíveis a qualquer usuário autenticado,
 * como {@code GET /api/v1/users/{id}}, garantindo que informações
 * pessoais como e-mail e e-mail corporativo não sejam expostos
 * a terceiros (prevenção de PII leak).</p>
 */
public record PublicUserResponseDto(
        UUID id,
        String name,
        String occupation,
        String bio,
        ExperienceLevel experienceLevel,
        List<String> hardSkills,
        List<String> softSkills,
        UserRole role,
        String companyName
) {}
