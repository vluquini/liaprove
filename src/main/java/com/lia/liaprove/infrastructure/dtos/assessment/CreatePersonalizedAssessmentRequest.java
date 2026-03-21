package com.lia.liaprove.infrastructure.dtos.assessment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreatePersonalizedAssessmentRequest(
    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Description is required")
    String description,

    @NotEmpty(message = "At least one question must be selected")
    List<UUID> questionIds,

    @NotNull(message = "Expiration date is required")
    @Future(message = "Expiration date must be in the future")
    LocalDateTime expirationDate,

    @Min(value = 1, message = "Max attempts must be at least 1")
    int maxAttempts,

    @Min(value = 5, message = "Evaluation timer must be at least 5 minutes")
    long evaluationTimerMinutes
) {}
