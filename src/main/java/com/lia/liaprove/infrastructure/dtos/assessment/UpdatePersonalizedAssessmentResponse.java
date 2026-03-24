package com.lia.liaprove.infrastructure.dtos.assessment;

import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdatePersonalizedAssessmentResponse(
        UUID assessmentId,
        LocalDateTime expirationDate,
        Integer maxAttempts,
        PersonalizedAssessmentStatus status
) {}
