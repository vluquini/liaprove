package com.lia.liaprove.application.services.metrics;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.metrics.FeedbackGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.SystemAssessment;
import com.lia.liaprove.core.domain.metrics.FeedbackAssessment;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.exceptions.user.InvalidUserDataException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import com.lia.liaprove.core.usecases.metrics.SubmitFeedbackOnAssessmentUseCase;

import java.time.LocalDateTime;
import java.util.UUID;

public class SubmitFeedbackOnAssessmentUseCaseImpl implements SubmitFeedbackOnAssessmentUseCase {

    private final FeedbackGateway feedbackGateway;
    private final UserGateway userGateway;
    private final AssessmentAttemptGateway assessmentAttemptGateway;

    public SubmitFeedbackOnAssessmentUseCaseImpl(FeedbackGateway feedbackGateway, UserGateway userGateway,
                                                 AssessmentAttemptGateway assessmentAttemptGateway) {
        this.feedbackGateway = feedbackGateway;
        this.userGateway = userGateway;
        this.assessmentAttemptGateway = assessmentAttemptGateway;
    }

    @Override
    public void submitFeedback(UUID userId, UUID attemptId, String comment) {
        User user = userGateway.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found."));

        AssessmentAttempt assessmentAttempt = assessmentAttemptGateway.findById(attemptId)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment attempt with id " + attemptId + " not found."));

        validateReviewEligibility(userId, assessmentAttempt);

        if (feedbackGateway.existsAssessmentFeedbackByUserIdAndAttemptId(userId, attemptId)) {
            throw new InvalidUserDataException("You have already submitted feedback for this assessment attempt.");
        }

        FeedbackAssessment feedback = new FeedbackAssessment(
                user,
                assessmentAttempt,
                comment,
                LocalDateTime.now(),
                true
        );

        feedbackGateway.saveAssessmentFeedback(feedback);
    }

    private void validateReviewEligibility(UUID userId, AssessmentAttempt attempt) {
        if (attempt.getUser() != null && attempt.getUser().getId().equals(userId)) {
            throw new AuthorizationException("You cannot interact with your own assessment attempt.");
        }

        if (attempt.getFinishedAt() == null) {
            throw new InvalidUserDataException("Only finished assessment attempts can receive feedback.");
        }

        if (!(attempt.getAssessment() instanceof SystemAssessment)) {
            throw new InvalidUserDataException("Only system mini-project assessment attempts can receive feedback.");
        }

        boolean hasProjectSubmission = attempt.getAnswers() != null
                && attempt.getAnswers().stream()
                .anyMatch(answer -> answer.getProjectUrl() != null && !answer.getProjectUrl().isBlank());

        if (!hasProjectSubmission) {
            throw new InvalidUserDataException("Only mini-project assessment attempts can receive feedback.");
        }
    }
}
