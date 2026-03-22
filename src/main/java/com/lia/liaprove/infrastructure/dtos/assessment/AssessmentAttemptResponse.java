package com.lia.liaprove.infrastructure.dtos.assessment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AssessmentAttemptResponse(
    UUID attemptId,
    String assessmentTitle,
    LocalDateTime startedAt,
    long evaluationTimerMinutes,
    List<AttemptQuestionResponse> questions
) {
    public record AttemptQuestionResponse(
        UUID id,
        String title,
        String description,
        // Using Object to allow flexibility for now (List<AlternativeDto> for MCQ)
        Object alternatives
    ) {}
}
