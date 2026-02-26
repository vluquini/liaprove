package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Caso de uso para atualizar os dados de uma Avaliação Personalizada.
 */
public interface UpdatePersonalizedAssessmentUseCase {

    /**
     * Atualiza os campos permitidos de uma avaliação personalizada (ex: data de expiração, limite de tentativas).
     *
     * @param assessmentId O ID da avaliação a ser atualizada.
     * @param requesterId  O ID do usuário (Recrutador/Admin) que solicita a atualização.
     * @param expirationDate Nova data de expiração (opcional).
     * @param maxAttempts Novo limite de tentativas (opcional).
     * @return A avaliação atualizada.
     * @throws AssessmentNotFoundException se a avaliação não for encontrada.
     * @throws AuthorizationException se o solicitante não for o dono ou um Admin.
     */
    Assessment execute(UUID assessmentId, UUID requesterId, LocalDateTime expirationDate, Integer maxAttempts);
}
