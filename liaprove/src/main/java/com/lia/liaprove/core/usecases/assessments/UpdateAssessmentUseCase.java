package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.exceptions.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.AuthorizationException;

import java.util.UUID;

/**
 * Caso de uso para atualizar uma avaliação personalizada existente.
 */
public interface UpdateAssessmentUseCase {

    // TODO: Definir um DTO para a atualização de Assessment

    /**
     * Atualiza os dados de uma avaliação personalizada.
     *
     * @param assessmentId O ID da avaliação a ser atualizada.
     * @param updatedAssessment O objeto com os dados atualizados.
     * @param userId O ID do usuário que está realizando a atualização.
     * @return A avaliação atualizada.
     * @throws AssessmentNotFoundException se a avaliação não for encontrada.
     * @throws AuthorizationException se o usuário não for o criador da avaliação ou não tiver permissão para atualizá-la.
     */
    Assessment execute(UUID assessmentId, Assessment updatedAssessment, UUID userId);
}
