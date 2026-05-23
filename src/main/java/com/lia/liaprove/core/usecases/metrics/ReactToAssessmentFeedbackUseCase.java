package com.lia.liaprove.core.usecases.metrics;

import com.lia.liaprove.core.domain.metrics.ReactionType;

import java.util.UUID;

public interface ReactToAssessmentFeedbackUseCase {
    void reactToFeedback(UUID userId, UUID feedbackId, ReactionType reactionType);
}
