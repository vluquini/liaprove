package com.lia.liaprove.infrastructure.dtos.metrics;

import java.time.LocalDateTime;
import java.util.UUID;

public record PublicMiniProjectAttemptResponse(
        UUID attemptId,
        String assessmentTitle,
        String authorName,
        String repositoryLink,
        LocalDateTime finishedAt
) {
}
