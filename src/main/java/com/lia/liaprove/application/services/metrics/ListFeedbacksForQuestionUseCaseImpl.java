package com.lia.liaprove.application.services.metrics;

import com.lia.liaprove.application.gateways.metrics.FeedbackGateway;
import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;
import com.lia.liaprove.core.exceptions.QuestionNotFoundException;
import com.lia.liaprove.core.usecases.metrics.ListFeedbacksForQuestionUseCase;

import java.util.List;
import java.util.UUID;

public class ListFeedbacksForQuestionUseCaseImpl implements ListFeedbacksForQuestionUseCase {

    private final FeedbackGateway feedbackGateway;
    private final QuestionGateway questionGateway;

    public ListFeedbacksForQuestionUseCaseImpl(FeedbackGateway feedbackGateway, QuestionGateway questionGateway) {
        this.feedbackGateway = feedbackGateway;
        this.questionGateway = questionGateway;
    }

    @Override
    public List<FeedbackQuestion> listFeedbacksForQuestion(UUID questionId) {
        questionGateway.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Question not found with id: " + questionId));

        return feedbackGateway.findFeedbacksByQuestionId(questionId);
    }
}
