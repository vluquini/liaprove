package com.lia.liaprove.infrastructure.dtos.algorithms.genetic;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record UpdateMultiplierRequest(
        @NotNull(message = "multiplier must not be null.")
        @DecimalMin(value = "0.0", message = "multiplier must be >= 0.0.")
        Double multiplier
) { }
