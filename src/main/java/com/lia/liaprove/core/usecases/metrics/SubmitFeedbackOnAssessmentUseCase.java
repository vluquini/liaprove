package com.lia.liaprove.core.usecases.metrics;

import java.util.UUID;

/**
 * Use case para permitir que um usuário envie um feedback sobre uma tentativa
 * específica de avaliação.
 */
public interface SubmitFeedbackOnAssessmentUseCase {
    /**
     * Envia um feedback textual sobre uma avaliação.
     *
     * @param userId O ID do usuário que está enviando o feedback.
     * @param attemptId O ID da tentativa que está recebendo o feedback.
     * @param comment O texto do feedback.
     */
    void submitFeedback(UUID userId, UUID attemptId, String comment);
}
