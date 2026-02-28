package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.application.services.assessment.dto.SubmitAssessmentAnswersDto;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.exceptions.assessment.AssessmentAttemptFinishedException;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;

import java.util.UUID;

/**
 * Caso de uso para submeter as respostas e finalizar uma tentativa de avaliação.
 */
public interface SubmitAssessmentUseCase {

    /**
     * Processa as respostas do usuário, finaliza a tentativa e calcula o resultado.
     *
     * @param submissionDto DTO contendo o ID da tentativa e as respostas.
     * @param userId        O ID do usuário que está submetendo a avaliação.
     * @return A tentativa de avaliação atualizada com o status final e nota (se aplicável).
     * @throws AssessmentNotFoundException        se a tentativa não for encontrada.
     * @throws AuthorizationException             se a tentativa não pertencer ao usuário.
     * @throws AssessmentAttemptFinishedException se a tentativa já estiver finalizada.
     */
    AssessmentAttempt execute(SubmitAssessmentAnswersDto submissionDto, UUID userId);
}
