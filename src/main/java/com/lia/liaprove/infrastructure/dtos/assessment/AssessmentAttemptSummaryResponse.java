package com.lia.liaprove.infrastructure.dtos.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.infrastructure.dtos.user.UserResponseDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssessmentAttemptSummaryResponse(
        UUID attemptId,
        AssessmentAttemptStatus status,
        Integer accuracyRate,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        AssessmentSummary assessment,
        UserResponseDto candidate
) {
    public record AssessmentSummary(
            UUID id,
            String title,
            boolean personalized,
            AssessmentCriteriaWeightsResponse criteriaWeights,
            JobDescriptionAnalysisResponse jobDescriptionAnalysis
    ) {}
}
