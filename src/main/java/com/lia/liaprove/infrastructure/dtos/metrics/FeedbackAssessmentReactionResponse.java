package com.lia.liaprove.infrastructure.dtos.metrics;

import com.lia.liaprove.core.domain.metrics.ReactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public record FeedbackAssessmentReactionResponse(
        UUID id,
        UUID userId,
        String userName,
        ReactionType type,
        LocalDateTime createdAt
) {
}
