package com.lia.liaprove.infrastructure.dtos.algorithms.genetic;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UpdateRecruiterVoteWeightsRequest(
        @NotEmpty(message = "weights must not be empty.")
        List<@Valid RecruiterVoteWeightItem> weights
) {
    public record RecruiterVoteWeightItem(
            @NotNull(message = "recruiterId must not be null.")
            UUID recruiterId,

            @NotNull(message = "weight must not be null.")
            @Min(value = 1, message = "weight must be >= 1.")
            @Max(value = 10, message = "weight must be <= 10.")
            Integer weight
    ) { }
}
