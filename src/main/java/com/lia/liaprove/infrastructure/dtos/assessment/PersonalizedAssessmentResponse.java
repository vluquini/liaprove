package com.lia.liaprove.infrastructure.dtos.assessment;

import java.util.UUID;

public record PersonalizedAssessmentResponse(
    UUID id,
    String title,
    String shareableToken,
    String status
) {}
