package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.exceptions.assessment.InvalidAttemptStatusException;

import java.util.UUID;

/**
 * Caso de uso para um Recrutador ou Admin finalizar manualmente uma tentativa de avaliação personalizada.
 */
public interface FinalizeAssessmentAttemptUseCase {

    /**
     * Define o status final de uma tentativa como APPROVED ou FAILED.
     * Esta ação não emite um certificado.
     *
     * @param attemptId   O ID da tentativa a ser finalizada.
     * @param requesterId O ID do Recrutador ou Admin executando a ação.
     * @param finalStatus O status final a ser definido (deve ser APPROVED ou FAILED).
     * @return A tentativa de avaliação atualizada.
     * @throws AssessmentNotFoundException     se a tentativa não for encontrada.
     * @throws AuthorizationException          se o solicitante não tiver permissão para finalizar a tentativa.
     * @throws InvalidAttemptStatusException   se a tentativa não estiver no estado 'COMPLETED'.
     * @throws IllegalArgumentException        se o 'finalStatus' não for APPROVED ou FAILED.
     */
    AssessmentAttempt execute(UUID attemptId, UUID requesterId, AssessmentAttemptStatus finalStatus);
}
