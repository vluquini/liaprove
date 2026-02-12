package com.lia.liaprove.application.services.metrics;

import com.lia.liaprove.application.gateways.metrics.FeedbackGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;
import com.lia.liaprove.core.domain.metrics.ReactionType;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.exceptions.AuthorizationException;
import com.lia.liaprove.core.exceptions.FeedbackNotFoundException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;
import com.lia.liaprove.core.usecases.metrics.ReactToFeedbackUseCase;

import java.util.UUID;

public class ReactToFeedbackUseCaseImpl implements ReactToFeedbackUseCase {

    private final FeedbackGateway feedbackGateway;
    private final UserGateway userGateway;

    public ReactToFeedbackUseCaseImpl(FeedbackGateway feedbackGateway, UserGateway userGateway) {
        this.feedbackGateway = feedbackGateway;
        this.userGateway = userGateway;
    }

    @Override
    public void reactToFeedback(UUID userId, UUID feedbackId, ReactionType reactionType) {
        User user = userGateway.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        FeedbackQuestion feedback = feedbackGateway.findFeedbackQuestionById(feedbackId)
                .orElseThrow(() -> new FeedbackNotFoundException("Feedback not found with id: " + feedbackId));

        // Business Rule: Users cannot react to their own feedback.
        if (feedback.getUser().getId().equals(userId)) {
            throw new AuthorizationException("Users cannot react to their own feedback.");
        }

        feedback.manageReaction(user, reactionType);

        feedbackGateway.saveFeedbackQuestion(feedback);
    }
}
