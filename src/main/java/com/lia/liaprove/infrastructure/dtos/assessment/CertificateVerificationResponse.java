package com.lia.liaprove.infrastructure.dtos.assessment;

import com.lia.liaprove.core.domain.user.ExperienceLevel;

import java.time.LocalDate;
import java.util.UUID;

public record CertificateVerificationResponse(
        String certificateNumber,
        String title,
        String description,
        String certificateUrl,
        LocalDate issueDate,
        Float score,
        OwnerSummary owner
) {
    public record OwnerSummary(
            UUID id,
            String name,
            String occupation,
            ExperienceLevel experienceLevel
    ) {}
}
