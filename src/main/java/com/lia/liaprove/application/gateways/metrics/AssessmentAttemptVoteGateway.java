package com.lia.liaprove.application.gateways.metrics;

import com.lia.liaprove.core.domain.metrics.AssessmentAttemptVote;

import java.util.UUID;

public interface AssessmentAttemptVoteGateway {
    void save(AssessmentAttemptVote vote);

    boolean existsByUserIdAndAttemptId(UUID userId, UUID attemptId);
}
