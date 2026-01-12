package com.lia.liaprove.application.services.user;

import com.lia.liaprove.core.domain.user.*;
import com.lia.liaprove.core.usecases.user.users.UserFactory;

import com.lia.liaprove.core.exceptions.InvalidUserDataException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
            case RECRUITER, ADMIN -> {
                UserRecruiter recruiter = new UserRecruiter();
                initRecruiterFields(recruiter, dto);
                user = recruiter;
            }
            default -> throw new IllegalArgumentException("Unsupported role: " + dto.role());
        }

        initCommonFields(user, dto);
        return user;
    }

    private void validateCommand(UserCreateDto dto) {
        Objects.requireNonNull(dto, "User creation data cannot be null");
        if (dto.name() == null || dto.name().isBlank()) {
            throw new InvalidUserDataException("Name must not be empty");
        }
        if (dto.email() == null || dto.email().isBlank() || !dto.email().contains("@")) {
            throw new InvalidUserDataException("Invalid email");
        }
        if (dto.passwordHash() == null || dto.passwordHash().isBlank()) {
            throw new InvalidUserDataException("Password hash must not be empty");
        }
        if (dto.role() == null) {
            throw new InvalidUserDataException("Role must be provided");
        }
    }

    private void initCommonFields(User user, UserCreateDto dto) {
        user.setName(trimOrNull(dto.name()));
        user.setEmail(normalizeEmail(dto.email()));
        user.setPasswordHash(dto.passwordHash());
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

    private void initRecruiterFields(UserRecruiter recruiter, UserCreateDto dto) {
        // Para o tipo RECRUITER, os campos da empresa são relevantes
        if (dto.role() == UserRole.RECRUITER) {
            recruiter.setCompanyName(trimOrEmpty(dto.companyName()));
            recruiter.setCompanyEmail(normalizeEmail(dto.companyEmail()));
        }
        // Inicializa outros campos específicos do Recruiter
        recruiter.setTotalAssessmentsCreated(0);
        recruiter.setRecruiterRating(0.0f);
        recruiter.setRecruiterRatingCount(0);
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
