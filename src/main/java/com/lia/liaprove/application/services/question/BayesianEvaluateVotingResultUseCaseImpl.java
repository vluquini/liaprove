package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.algorithms.bayesian.BayesianVotingDecision;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.exceptions.question.QuestionNotFoundException;
import com.lia.liaprove.core.usecases.algorithms.bayesian.BayesianNetworkUseCase;
import com.lia.liaprove.core.usecases.question.EvaluateVotingResultUseCase;

import java.util.Objects;
import java.util.UUID;

/**
 * Implementação real do fluxo de avaliação bayesiana até a camada application.
 * A infraestrutura ainda pode optar por usar a implementação mock para fins demonstrativos.
 */
public class BayesianEvaluateVotingResultUseCaseImpl implements EvaluateVotingResultUseCase {
    private final QuestionGateway questionGateway;
    private final BayesianNetworkUseCase bayesianNetworkUseCase;

    public BayesianEvaluateVotingResultUseCaseImpl(QuestionGateway questionGateway,
                                                   BayesianNetworkUseCase bayesianNetworkUseCase) {
        this.questionGateway = Objects.requireNonNull(questionGateway, "questionGateway must not be null");
        this.bayesianNetworkUseCase = Objects.requireNonNull(bayesianNetworkUseCase,
                "bayesianNetworkUseCase must not be null");
    }

    @Override
    public Question evaluate(UUID questionId) {
        Question question = questionGateway.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Question with id " + questionId + " not found."));

        BayesianVotingDecision decision = bayesianNetworkUseCase.evaluateVotingResult(questionId);
        question.setStatus(decision.getResultStatus());

        return questionGateway.save(question);
    }
}
