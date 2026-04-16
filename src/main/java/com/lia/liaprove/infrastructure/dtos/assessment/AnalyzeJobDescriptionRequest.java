package com.lia.liaprove.infrastructure.dtos.assessment;

import jakarta.validation.constraints.NotBlank;

public record AnalyzeJobDescriptionRequest(
        @NotBlank(message = "Job description is required")
        String jobDescription
) {}
