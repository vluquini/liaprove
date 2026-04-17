package com.lia.liaprove.infrastructure.dtos.assessment;

import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public record UpdatePersonalizedAssessmentRequest(
        @Future LocalDateTime expirationDate,
        @Min(1) Integer maxAttempts,
        PersonalizedAssessmentStatus status,
        @Min(0) Integer hardSkillsWeight,
        @Min(0) Integer softSkillsWeight,
        @Min(0) Integer experienceWeight
) {
    public UpdatePersonalizedAssessmentRequest(
            LocalDateTime expirationDate,
            Integer maxAttempts,
            PersonalizedAssessmentStatus status
    ) {
        this(expirationDate, maxAttempts, status, null, null, null);
    }
}
