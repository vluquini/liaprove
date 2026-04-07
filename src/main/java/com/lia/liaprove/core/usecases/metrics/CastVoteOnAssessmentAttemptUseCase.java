package com.lia.liaprove.core.usecases.metrics;

import com.lia.liaprove.core.domain.metrics.VoteType;

import java.util.UUID;

public interface CastVoteOnAssessmentAttemptUseCase {
    void castVote(UUID userId, UUID attemptId, VoteType voteType);
}
