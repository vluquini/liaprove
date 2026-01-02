package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.exceptions.QuestionNotFoundException;
import com.lia.liaprove.core.usecases.question.EvaluateVotingResultUseCase;

import java.util.Random;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MockEvaluateVotingResultUseCaseImpl implements EvaluateVotingResultUseCase {

    private final QuestionGateway questionGateway;
    private final Random random = new Random();

    public MockEvaluateVotingResultUseCaseImpl(QuestionGateway questionGateway) {
        this.questionGateway = questionGateway;
    }

    @Override
    public Question evaluate(UUID questionId) {
        Question question = questionGateway.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Question with id " + questionId + " not found."));

        // Simulate Bayesian algorithm decision (temporary mock logic)
        if (random.nextBoolean()) {
            question.setStatus(QuestionStatus.APPROVED);
        } else {
            question.setStatus(QuestionStatus.REJECTED);
        }

        return questionGateway.save(question);
    }
}
