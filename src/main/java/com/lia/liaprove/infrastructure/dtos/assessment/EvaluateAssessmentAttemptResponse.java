package com.lia.liaprove.infrastructure.dtos.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;

public record EvaluateAssessmentAttemptResponse(
        AssessmentAttemptStatus status,
        Integer accuracyRate,
        String message
) {}

