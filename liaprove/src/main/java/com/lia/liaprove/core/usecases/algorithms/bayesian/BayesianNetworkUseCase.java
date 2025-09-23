package com.lia.liaprove.core.usecases.algorithms.bayesian;

import com.lia.liaprove.core.algorithms.bayesian.ScoredQuestion;
import com.lia.liaprove.core.domain.question.Question;

import java.util.List;
import java.util.UUID;

/**
 * Interface de alto nível para operações Bayesianas.
 */
public interface BayesianNetworkUseCase {

    /**
     * Calcula uma pontuação (probabilidade) de aprovação para a questão dada,
     * considerando o contexto do recruiter (p.ex. preferências).
     */
    double probabilityQuestionApproved(Question q, UUID recruiterId);

    /**
     * Sugere as melhores questões para um recruiter ao criar uma avaliação personalizada.
     * Implementação típica: ordena perguntas por score + filtros de área/nivel.
     */
    List<ScoredQuestion> suggestQuestionsForRecruiter(UUID recruiterId, int limit);
}
