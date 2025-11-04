package com.lia.liaprove.core.usecases.metrics;

import java.util.UUID;

/**
 * Use case para permitir que um usuário envie um feedback sobre uma avaliação personalizada.
 */
public interface SubmitFeedbackOnAssessmentUseCase {
    /**
     * Envia um feedback textual sobre uma avaliação.
     *
     * @param userId O ID do usuário que está enviando o feedback.
     * @param assessmentId O ID da avaliação que está recebendo o feedback.
     * @param comment O texto do feedback.
     */
    void submitFeedback(UUID userId, UUID assessmentId, String comment);
}
