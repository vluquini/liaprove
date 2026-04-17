package com.lia.liaprove.infrastructure.dtos.assessment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AttemptPreAnalysisResponse(
        Metadata metadata,
        Analysis analysis
) {
    public record Metadata(
            UUID attemptId,
            LocalDateTime generatedAt,
            List<String> ignoredQuestionTypes
    ) {}

    public record Analysis(
            String summary,
            List<String> strengths,
            List<String> attentionPoints,
            String finalExplanation
    ) {}
}
