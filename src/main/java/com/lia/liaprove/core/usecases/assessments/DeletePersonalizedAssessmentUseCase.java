package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.exceptions.assessment.AssessmentInUseException;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;

import java.util.UUID;

/**
 * Caso de uso para excluir uma Avaliação Personalizada.
 */
public interface DeletePersonalizedAssessmentUseCase {

    /**
     * Exclui uma avaliação personalizada, desde que não existam tentativas associadas a ela.
     *
     * @param assessmentId O ID da avaliação a ser excluída.
     * @param requesterId  O ID do usuário (Recrutador/Admin) que solicita a exclusão.
     * @throws AssessmentNotFoundException se a avaliação não for encontrada.
     * @throws AuthorizationException se o solicitante não for o dono ou um Admin.
     * @throws AssessmentInUseException se a avaliação já possuir tentativas e não puder ser excluída.
     */
    void execute(UUID assessmentId, UUID requesterId);
}
