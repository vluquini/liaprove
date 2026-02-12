package com.lia.liaprove.infrastructure.dtos.metrics;

import com.lia.liaprove.core.domain.metrics.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackReactionResponse {
    private UUID id;
    private UUID userId;
    private String userName;
    private ReactionType type;
    private LocalDateTime createdAt;
}
