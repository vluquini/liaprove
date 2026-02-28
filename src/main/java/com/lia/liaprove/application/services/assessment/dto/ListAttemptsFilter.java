package com.lia.liaprove.application.services.assessment.dto;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * DTO para encapsular os critérios de filtro ao listar tentativas de avaliação.
 * Usado pelo gateway para construir queries de busca.
 */
public record ListAttemptsFilter(
        Optional<Boolean> isPersonalized,
        Optional<LocalDateTime> startDate,
        Optional<LocalDateTime> endDate,
        Optional<Set<AssessmentAttemptStatus>> statuses
) {}
