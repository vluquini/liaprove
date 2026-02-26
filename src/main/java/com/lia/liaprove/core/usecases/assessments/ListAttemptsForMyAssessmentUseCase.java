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
     * Retorna a lista de tentativas para uma avaliação, validando se o solicitante é o criador.
     *
     * @param assessmentId O ID da avaliação.
     * @param recruiterId  O ID do recrutador que está solicitando.
     * @return Uma lista de objetos {@link AssessmentAttempt}.
     * @throws AssessmentNotFoundException se a avaliação não for encontrada.
     * @throws AuthorizationException      se o recrutador não for o criador da avaliação.
     */
    List<AssessmentAttempt> execute(UUID assessmentId, UUID recruiterId);
}
