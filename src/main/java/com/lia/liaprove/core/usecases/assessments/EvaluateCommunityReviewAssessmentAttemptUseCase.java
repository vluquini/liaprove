package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;

import java.util.UUID;

/**
 * Avalia o resultado comunitário de uma tentativa de mini-projeto do sistema
 * após o encerramento da janela de votação.
 */
public interface EvaluateCommunityReviewAssessmentAttemptUseCase {
    AssessmentAttempt execute(UUID attemptId);
}
