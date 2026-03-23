package com.lia.liaprove.infrastructure.dtos.assessment;

import java.util.UUID;

public record DeletePersonalizedAssessmentResponse(
        UUID assessmentId,
        String message
) {}

