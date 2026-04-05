package com.lia.liaprove.infrastructure.dtos.metrics;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record SubmitFeedbackOnAssessmentRequest(
        UUID userId,
        UUID assessmentId,

        @NotEmpty(message = "Comment cannot be empty")
        @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
        String comment
) {}
