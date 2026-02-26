package com.lia.liaprove.application.gateways.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;

import java.util.Optional;
import java.util.UUID;

/**
 * Gateway para operações de persistência da entidade AssessmentAttempt.
 */
public interface AssessmentAttemptGateway {
    /**
     * Salva uma nova tentativa de avaliação.
     * @param attempt A tentativa a ser salva.
     * @return A tentativa salva.
     */
    AssessmentAttempt save(AssessmentAttempt attempt);

    /**
     * Recupera uma tentativa de avaliação pelo seu ID.
     * @param id O ID da tentativa.
     * @return Um Optional contendo a tentativa encontrada, ou vazio se não existir.
     */
    Optional<AssessmentAttempt> findById(UUID id);

    /**
     * Conta o número de tentativas para uma avaliação específica.
     * @param assessmentId O ID da avaliação.
     * @return O número total de tentativas.
     */
    long countByAssessmentId(UUID assessmentId);

    /**
     * Verifica se um usuário específico já tentou uma avaliação.
     * @param assessmentId O ID da avaliação.
     * @param userId O ID do usuário.
     * @return true se já existe uma tentativa, false caso contrário.
     */
    boolean existsByAssessmentIdAndUserId(UUID assessmentId, UUID userId);
}
