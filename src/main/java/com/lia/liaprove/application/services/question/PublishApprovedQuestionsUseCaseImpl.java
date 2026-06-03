package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.usecases.question.PublishApprovedQuestionsUseCase;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class PublishApprovedQuestionsUseCaseImpl implements PublishApprovedQuestionsUseCase {

    private final QuestionGateway questionGateway;

    public PublishApprovedQuestionsUseCaseImpl(QuestionGateway questionGateway) {
        this.questionGateway = questionGateway;
    }

    @Override
    public int publishEligibleQuestions(Duration publicationDelay, LocalDateTime now) {
        Objects.requireNonNull(publicationDelay, "publicationDelay must not be null");
        Objects.requireNonNull(now, "now must not be null");

        List<Question> eligibleQuestions;
        if (publicationDelay.isZero()) {
            eligibleQuestions = questionGateway.findByStatus(QuestionStatus.APPROVED);
        } else {
            LocalDateTime cutoff = now.minus(publicationDelay);
            eligibleQuestions = questionGateway.findByStatusAndVotingEndDateBefore(QuestionStatus.APPROVED, cutoff);
        }

        eligibleQuestions.forEach(question -> {
            question.setStatus(QuestionStatus.FINISHED);
            questionGateway.update(question);
        });

        return eligibleQuestions.size();
    }
}
