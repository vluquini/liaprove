package com.lia.liaprove.infrastructure.dtos.metrics;

import com.lia.liaprove.core.domain.metrics.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReactToFeedbackRequest {
    @NotNull(message = "Reaction type cannot be null")
    private ReactionType reactionType;
}
