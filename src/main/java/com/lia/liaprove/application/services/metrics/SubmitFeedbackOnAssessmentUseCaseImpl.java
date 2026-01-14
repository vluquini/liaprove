package com.lia.liaprove.application.services.metrics;

import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.application.gateways.metrics.FeedbackGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.metrics.FeedbackAssessment;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.exceptions.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;
import com.lia.liaprove.core.usecases.metrics.SubmitFeedbackOnAssessmentUseCase;

import java.time.LocalDateTime;
import java.util.UUID;

public class SubmitFeedbackOnAssessmentUseCaseImpl implements SubmitFeedbackOnAssessmentUseCase {

    private final FeedbackGateway feedbackGateway;
    private final UserGateway userGateway;
    private final AssessmentGateway assessmentGateway;

    public SubmitFeedbackOnAssessmentUseCaseImpl(FeedbackGateway feedbackGateway, UserGateway userGateway,
                                                 AssessmentGateway assessmentGateway) {
        this.feedbackGateway = feedbackGateway;
        this.userGateway = userGateway;
        this.assessmentGateway = assessmentGateway;
    }

    @Override
    public void submitFeedback(UUID userId, UUID assessmentId, String comment) {
        User user = userGateway.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found."));

        Assessment assessment = assessmentGateway.findById(assessmentId)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment with id " + assessmentId + " not found."));

        FeedbackAssessment feedback = new FeedbackAssessment(
                user,
                assessment,
                comment,
                LocalDateTime.now(),
                true // By default deixamos o feedback visível
        );

        feedbackGateway.saveAssessmentFeedback(feedback);
    }
}
