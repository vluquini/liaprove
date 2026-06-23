package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.user.User;

import java.util.Objects;

public class AssessmentAttemptVote extends Vote {
    private AssessmentAttempt assessmentAttempt;

    public AssessmentAttemptVote() {
    }

    public AssessmentAttemptVote(User user, AssessmentAttempt assessmentAttempt, VoteType voteType) {
        super(user, voteType);
        this.assessmentAttempt = Objects.requireNonNull(assessmentAttempt, "assessmentAttempt cannot be null");
    }

    public AssessmentAttempt getAssessmentAttempt() {
        return assessmentAttempt;
    }

    public void setAssessmentAttempt(AssessmentAttempt assessmentAttempt) {
        if (this.assessmentAttempt != null && !this.assessmentAttempt.equals(assessmentAttempt)) {
            throw new IllegalStateException("Assessment attempt has already been set and cannot be changed.");
        }
        this.assessmentAttempt = assessmentAttempt;
    }
}
