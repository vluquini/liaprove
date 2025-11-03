package com.lia.liaprove.application.services.user;

import com.lia.liaprove.core.domain.user.*;
import com.lia.liaprove.core.usecases.user.users.UserFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementação da Factory para a criação de diferentes tipos de usuários.
 * Centraliza a lógica de instanciação e configuração inicial das entidades de User.
 */
public class DefaultUserFactory implements UserFactory {

    @Override
    public User create(UserCreateDto dto) {
        validateCommand(dto);

        User user;
        switch (dto.role()) {
            case PROFESSIONAL -> user = new UserProfessional();
            case RECRUITER, ADMIN -> user = new UserRecruiter();
            default -> throw new IllegalArgumentException("Unsupported role: " + dto.role());
        }

        initCommonFields(user, dto);
        return user;
    }

    private void validateCommand(UserCreateDto dto) {
        Objects.requireNonNull(dto, "command");
        if (dto.name() == null || dto.name().isBlank()) throw new IllegalArgumentException("name is required");
        if (dto.email() == null || dto.email().isBlank() || !dto.email().contains("@")) throw new IllegalArgumentException("invalid email");
        if (dto.passwordHash() == null || dto.passwordHash().isBlank()) throw new IllegalArgumentException("passwordHash is required");
        if (dto.role() == null) throw new IllegalArgumentException("role is required");
    }

    private void initCommonFields(User user, UserCreateDto dto) {
        user.setId(UUID.randomUUID());
        user.setName(trimOrNull(dto.name()));
        user.setEmail(normalizeEmail(dto.email()));
        user.setPasswordHash(dto.passwordHash()); // hash vem da infra
        user.setOccupation(trimOrEmpty(dto.occupation()));
        user.setBio("");
        user.setExperienceLevel(dto.experienceLevel() == null ? ExperienceLevel.JUNIOR : dto.experienceLevel());
        user.setRole(dto.role());
        user.setVoteWeight(1);
        user.setTotalAssessmentsTaken(0);
        user.setCertificates(List.of());
        user.setAverageScore(0f);
        user.setRegistrationDate(LocalDateTime.now());
        user.setLastLogin(null);
        user.setStatus(UserStatus.ACTIVE);
    }

    private static String trimOrNull(String s) {
        return s == null ? null : s.trim();
    }

    private static String trimOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private static String normalizeEmail(String e) {
        return e == null ? null : e.trim().toLowerCase();
    }

}
