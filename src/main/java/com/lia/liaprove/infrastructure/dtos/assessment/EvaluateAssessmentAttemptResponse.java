package com.lia.liaprove.infrastructure.dtos.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.infrastructure.dtos.user.UserResponseDto;

import java.util.UUID;

public record EvaluateAssessmentAttemptResponse(
        UUID attemptId,
        AssessmentAttemptStatus status,
        Integer accuracyRate,
        AssessmentAttemptSummaryResponse.AssessmentSummary assessment,
        UserResponseDto candidate,
        AssessmentExplainabilityResponse explainability,
        String message
) {}

