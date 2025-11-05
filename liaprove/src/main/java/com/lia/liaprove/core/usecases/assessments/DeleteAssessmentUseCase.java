package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.exceptions.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.AuthorizationException;

import java.util.UUID;

/**
 * Caso de uso para remover uma avaliação personalizada.
 */
public interface DeleteAssessmentUseCase {

    /**
     * Remove uma avaliação personalizada do sistema.
     *
     * @param assessmentId O ID da avaliação a ser removida.
     * @param userId O ID do usuário que está tentando remover a avaliação.
     * @throws AssessmentNotFoundException se a avaliação não for encontrada.
     * @throws AuthorizationException se o usuário não for o criador da avaliação ou não tiver permissão para removê-la.
     */
    void execute(UUID assessmentId, UUID userId);
}
