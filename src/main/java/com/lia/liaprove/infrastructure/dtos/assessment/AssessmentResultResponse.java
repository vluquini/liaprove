package com.lia.liaprove.infrastructure.dtos.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;

public record AssessmentResultResponse(
    AssessmentAttemptStatus status,
    Integer accuracyRate,
    String certificateUrl,
    String message
) {}
