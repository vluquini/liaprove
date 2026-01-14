package com.lia.liaprove.application.services.metrics;

import com.lia.liaprove.application.gateways.metrics.FeedbackGateway;
import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.exceptions.QuestionNotFoundException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;
import com.lia.liaprove.core.usecases.metrics.SubmitFeedbackOnQuestionUseCase;

import java.time.LocalDateTime;
import java.util.UUID;

public class SubmitFeedbackOnQuestionUseCaseImpl implements SubmitFeedbackOnQuestionUseCase {

    private final FeedbackGateway feedbackGateway;
    private final UserGateway userGateway;
    private final QuestionGateway questionGateway;

    public SubmitFeedbackOnQuestionUseCaseImpl(FeedbackGateway feedbackGateway, UserGateway userGateway,
                                               QuestionGateway questionGateway) {
        this.feedbackGateway = feedbackGateway;
        this.userGateway = userGateway;
        this.questionGateway = questionGateway;
    }

    @Override
    public void submitFeedback(UUID userId, UUID questionId, String comment, DifficultyLevel difficultyLevel,
                               KnowledgeArea knowledgeArea, RelevanceLevel relevanceLevel) {
        User user = userGateway.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found."));

        Question question = questionGateway.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Question with id " + questionId + " not found."));

        FeedbackQuestion feedback = new FeedbackQuestion(
                user,
                comment,
                LocalDateTime.now(),
                question,
                difficultyLevel,
                knowledgeArea,
                relevanceLevel
        ); // Assuming feedback is visible by default

        feedbackGateway.saveFeedbackQuestion(feedback);
    }
}
