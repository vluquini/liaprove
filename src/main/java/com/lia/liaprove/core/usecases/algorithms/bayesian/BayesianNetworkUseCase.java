package com.lia.liaprove.core.usecases.algorithms.bayesian;

import com.lia.liaprove.core.algorithms.bayesian.BayesianVotingDecision;
import com.lia.liaprove.core.algorithms.bayesian.ScoredQuestion;

import java.util.List;
import java.util.UUID;

/**
 * Interface de alto nível para operações Bayesianas.
 */
public interface BayesianNetworkUseCase {

    /**
     * Sugere as melhores questões para um recruiter ao criar uma avaliação personalizada.
     * Implementação típica: ordena perguntas por score + filtros de área/nivel.
     */
    List<ScoredQuestion> suggestQuestionsForRecruiter(UUID recruiterId, int limit);

    /**
     * Avalia o resultado bayesiano de uma questão em votação.
     */
    BayesianVotingDecision evaluateVotingResult(UUID questionId);
}
