package com.lia.liaprove.application.services.metrics;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.metrics.FeedbackAssessment;
import com.lia.liaprove.core.domain.question.ProjectQuestion;

import java.util.List;

public record PublicMiniProjectAttemptDetails(
        AssessmentAttempt attempt,
        ProjectQuestion question,
        String repositoryLink,
        String textResponse,
        long approveVotes,
        long rejectVotes,
        List<FeedbackAssessment> feedbacks
) {
}
