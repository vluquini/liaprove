package com.lia.liaprove.application.services.metrics;

import com.lia.liaprove.application.gateways.metrics.FeedbackGateway;
import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;
import com.lia.liaprove.core.exceptions.AuthorizationException;
import com.lia.liaprove.core.exceptions.FeedbackNotFoundException;
import com.lia.liaprove.core.usecases.metrics.UpdateFeedbackCommentUseCase;

import java.util.UUID;

public class UpdateFeedbackCommentUseCaseImpl implements UpdateFeedbackCommentUseCase {

    private final FeedbackGateway feedbackGateway;

    public UpdateFeedbackCommentUseCaseImpl(FeedbackGateway feedbackGateway) {
        this.feedbackGateway = feedbackGateway;
    }

    @Override
    public void execute(UUID actorId, UUID feedbackId, String newComment) {
        FeedbackQuestion feedback = feedbackGateway.findFeedbackQuestionById(feedbackId)
                .orElseThrow(() -> new FeedbackNotFoundException("Feedback not found with id: " + feedbackId));

        // Rule: Only the owner can edit their comment.
        if (!feedback.getUser().getId().equals(actorId)) {
            throw new AuthorizationException("Users can only edit their own feedback comments.");
        }

        feedback.editComment(newComment);
        feedbackGateway.saveFeedbackQuestion(feedback);
    }
}
