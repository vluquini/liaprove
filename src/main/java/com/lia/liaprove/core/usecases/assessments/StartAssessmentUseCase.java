package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.exceptions.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;

import java.util.UUID;

/**
 * Caso de uso para iniciar uma nova tentativa de avaliação para um usuário.
 */
public interface StartAssessmentUseCase {

    /**
     * Cria uma nova entidade AssessmentAttempt com o status IN_PROGRESS.
     *
     * @param assessmentId O ID da avaliação a ser iniciada.
     * @param userId O ID do usuário que está realizando a avaliação.
     * @return O objeto AssessmentAttempt recém-criado.
     * @throws AssessmentNotFoundException se a avaliação não for encontrada ou estiver inativa.
     * @throws UserNotFoundException se o usuário não for encontrado.
     * @throws IllegalStateException se o usuário já tiver uma tentativa em andamento para esta avaliação.
     */
    AssessmentAttempt execute(UUID assessmentId, UUID userId);
}
