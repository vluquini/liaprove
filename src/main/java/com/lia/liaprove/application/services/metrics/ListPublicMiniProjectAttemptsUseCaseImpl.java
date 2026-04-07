package com.lia.liaprove.application.services.metrics;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.usecases.metrics.ListPublicMiniProjectAttemptsUseCase;

import java.util.List;
import java.util.UUID;

public class ListPublicMiniProjectAttemptsUseCaseImpl implements ListPublicMiniProjectAttemptsUseCase {

    private final AssessmentAttemptGateway assessmentAttemptGateway;

    public ListPublicMiniProjectAttemptsUseCaseImpl(AssessmentAttemptGateway assessmentAttemptGateway) {
        this.assessmentAttemptGateway = assessmentAttemptGateway;
    }

    @Override
    public List<AssessmentAttempt> list(UUID currentUserId) {
        return assessmentAttemptGateway.findPublicSystemProjectAttemptsExcludingUser(currentUserId);
    }
}
