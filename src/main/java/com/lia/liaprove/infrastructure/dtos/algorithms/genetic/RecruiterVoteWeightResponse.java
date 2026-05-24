package com.lia.liaprove.infrastructure.dtos.algorithms.genetic;

import java.util.UUID;

public record RecruiterVoteWeightResponse(
        UUID id,
        String name,
        String email,
        String companyName,
        String companyEmail,
        Integer voteWeight,
        Double multiplier
) {
}
