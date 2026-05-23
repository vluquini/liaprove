package com.lia.liaprove.application.gateways.metrics;

import com.lia.liaprove.core.domain.metrics.AssessmentAttemptVote;

import java.util.List;
import java.util.UUID;

public interface AssessmentAttemptVoteGateway {
    void save(AssessmentAttemptVote vote);

    List<AssessmentAttemptVote> findByAttemptId(UUID attemptId);

    boolean existsByUserIdAndAttemptId(UUID userId, UUID attemptId);
}
