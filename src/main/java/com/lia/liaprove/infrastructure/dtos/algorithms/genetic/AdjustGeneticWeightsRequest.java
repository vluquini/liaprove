package com.lia.liaprove.infrastructure.dtos.algorithms.genetic;

import jakarta.validation.constraints.NotNull;

public record AdjustGeneticWeightsRequest(
        @NotNull(message = "dryRun must not be null.")
        Boolean dryRun
) { }
