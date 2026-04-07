package com.lia.liaprove.core.usecases.metrics;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;

import java.util.List;
import java.util.UUID;

public interface ListPublicMiniProjectAttemptsUseCase {
    List<AssessmentAttempt> list(UUID currentUserId);
}
