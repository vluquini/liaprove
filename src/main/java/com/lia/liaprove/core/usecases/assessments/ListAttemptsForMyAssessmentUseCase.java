package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso para listar as tentativas de uma avaliação específica.
 */
public interface ListAttemptsForMyAssessmentUseCase {

    /**
     * Retorna a lista de tentativas para uma avaliação, validando se o solicitante é admin ou criador.
     *
     * @param assessmentId O ID da avaliação.
     * @param requesterId  O ID do usuário que está solicitando.
     * @return Uma lista de objetos {@link AssessmentAttempt}.
     * @throws AssessmentNotFoundException se a avaliação não for encontrada.
     * @throws AuthorizationException      se o usuário não tiver permissão para visualizar as tentativas.
     */
    List<AssessmentAttempt> execute(UUID assessmentId, UUID requesterId);
}
