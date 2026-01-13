package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.exceptions.QuestionNotFoundException;
import com.lia.liaprove.core.usecases.question.EvaluateVotingResultUseCase;

import java.util.Random;
import java.util.UUID;

/**
 * Implementação mock (simulada) do {@link EvaluateVotingResultUseCase}.
 * <p>
 * Esta classe simula a avaliação dos resultados da votação de uma questão. Em vez de
 * aplicar um algoritmo Bayesiano real, ela define aleatoriamente o status da questão para
 * {@link QuestionStatus#APPROVED} ou {@link QuestionStatus#REJECTED}.
 * <p>
 * Esta implementação é destinada a fins de desenvolvimento e teste, permitindo que o
 * ciclo de vida da questão seja concluído sem a necessidade de implementar o domínio completo
 * de feedback e votação.
 */
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
