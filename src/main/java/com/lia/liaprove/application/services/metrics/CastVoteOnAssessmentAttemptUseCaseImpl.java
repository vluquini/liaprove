package com.lia.liaprove.application.services.metrics;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.metrics.AssessmentAttemptVoteGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.SystemAssessment;
import com.lia.liaprove.core.domain.metrics.AssessmentAttemptVote;
import com.lia.liaprove.core.domain.metrics.VoteType;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.exceptions.user.InvalidUserDataException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import com.lia.liaprove.core.usecases.metrics.CastVoteOnAssessmentAttemptUseCase;

import java.util.UUID;

public class CastVoteOnAssessmentAttemptUseCaseImpl implements CastVoteOnAssessmentAttemptUseCase {

    private final UserGateway userGateway;
    private final AssessmentAttemptGateway assessmentAttemptGateway;
    private final AssessmentAttemptVoteGateway assessmentAttemptVoteGateway;

    public CastVoteOnAssessmentAttemptUseCaseImpl(UserGateway userGateway,
                                                  AssessmentAttemptGateway assessmentAttemptGateway,
                                                  AssessmentAttemptVoteGateway assessmentAttemptVoteGateway) {
        this.userGateway = userGateway;
        this.assessmentAttemptGateway = assessmentAttemptGateway;
        this.assessmentAttemptVoteGateway = assessmentAttemptVoteGateway;
    }

    @Override
    public void castVote(UUID userId, UUID attemptId, VoteType voteType) {
        User user = userGateway.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        AssessmentAttempt attempt = assessmentAttemptGateway.findById(attemptId)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment attempt with id " + attemptId + " not found."));

        validateReviewEligibility(userId, attempt);

        if (assessmentAttemptVoteGateway.existsByUserIdAndAttemptId(userId, attemptId)) {
            throw new InvalidUserDataException("You have already voted on this assessment attempt.");
        }

        AssessmentAttemptVote vote = new AssessmentAttemptVote(user, attempt, voteType);
        assessmentAttemptVoteGateway.save(vote);
    }

    private void validateReviewEligibility(UUID userId, AssessmentAttempt attempt) {
        if (attempt.getUser() != null && attempt.getUser().getId().equals(userId)) {
            throw new AuthorizationException("You cannot interact with your own assessment attempt.");
        }

        if (attempt.getFinishedAt() == null) {
            throw new InvalidUserDataException("Only finished assessment attempts can receive votes.");
        }

        if (!(attempt.getAssessment() instanceof SystemAssessment)) {
            throw new InvalidUserDataException("Only system mini-project assessment attempts can receive votes.");
        }

        boolean hasProjectSubmission = attempt.getAnswers() != null
                && attempt.getAnswers().stream()
                .anyMatch(answer -> answer.getProjectUrl() != null && !answer.getProjectUrl().isBlank());

        if (!hasProjectSubmission) {
            throw new InvalidUserDataException("Only mini-project assessment attempts can receive votes.");
        }
    }
}
