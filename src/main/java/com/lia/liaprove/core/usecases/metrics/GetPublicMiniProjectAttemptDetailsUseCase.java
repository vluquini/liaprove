package com.lia.liaprove.core.usecases.metrics;

import com.lia.liaprove.application.services.metrics.PublicMiniProjectAttemptDetails;

import java.util.Optional;
import java.util.UUID;

public interface GetPublicMiniProjectAttemptDetailsUseCase {
    Optional<PublicMiniProjectAttemptDetails> execute(UUID attemptId, UUID currentUserId);
}
