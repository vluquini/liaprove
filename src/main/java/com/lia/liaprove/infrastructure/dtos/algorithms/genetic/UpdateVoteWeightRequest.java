package com.lia.liaprove.infrastructure.dtos.algorithms.genetic;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateVoteWeightRequest(
        @NotNull(message = "weight must not be null.")
        @Min(value = 1, message = "weight must be >= 1.")
        @Max(value = 10, message = "weight must be <= 10.")
        Integer weight
) { }
