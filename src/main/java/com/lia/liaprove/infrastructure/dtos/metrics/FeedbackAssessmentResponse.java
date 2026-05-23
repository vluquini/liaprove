package com.lia.liaprove.infrastructure.dtos.metrics;

import com.lia.liaprove.infrastructure.dtos.user.AuthorDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FeedbackAssessmentResponse(
        UUID id,
        String comment,
        AuthorDto author,
        LocalDateTime submissionDate,
        List<FeedbackAssessmentReactionResponse> reactions
) {
}
