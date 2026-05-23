package com.lia.liaprove.infrastructure.dtos.metrics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PublicMiniProjectAttemptDetailResponse(
        UUID attemptId,
        String assessmentTitle,
        String authorName,
        LocalDateTime finishedAt,
        String repositoryLink,
        String textResponse,
        PublicMiniProjectQuestionResponse question,
        VoteSummaryResponse voteSummary,
        List<FeedbackAssessmentResponse> feedbacks
) {
}
