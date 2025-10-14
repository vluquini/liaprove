package com.lia.liaprove.core.usecases.user.assessments;

import com.lia.liaprove.core.exceptions.UserNotFoundException;

import java.util.UUID;

/**
 * Registra o resultado de uma tentativa de avaliação — atualiza métricas do usuário e, quando aplicável,
 * solicita emissão de certificados (orquestração).
 */
public interface RecordAssessmentResultUseCase {
    void recordResult(UUID userId, UUID assessmentId, float score) throws UserNotFoundException;
}