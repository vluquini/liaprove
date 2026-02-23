package com.lia.liaprove.application.services.assessment.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para criação de uma Avaliação Personalizada por um Recrutador.
 */
public record PersonalizedAssessmentCreateDto(
        String title,
        String description,
        List<UUID> questionIds,
        LocalDateTime expirationDate,
        int maxAttempts,
        long evaluationTimerMinutes
) {}
