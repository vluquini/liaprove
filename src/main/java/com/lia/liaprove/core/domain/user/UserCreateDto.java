package com.lia.liaprove.core.domain.user;

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
        String companyName,
        String companyEmail
) {}
