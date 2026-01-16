package com.lia.liaprove.infrastructure.dtos.metrics;

import com.lia.liaprove.core.domain.metrics.VoteType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CastVoteRequest {
    @NotNull(message = "Vote type cannot be null")
    private VoteType voteType;
}
