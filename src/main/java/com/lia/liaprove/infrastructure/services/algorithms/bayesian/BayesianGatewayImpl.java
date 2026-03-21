package com.lia.liaprove.infrastructure.services.algorithms.bayesian;

import com.lia.liaprove.application.gateways.algorithms.bayesian.BayesianGateway;
import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.algorithms.bayesian.QuestionFeedbackSummary;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Implementação TEMPORÁRIA do BayesianGateway.
 * Esta classe serve para viabilizar a execução do motor de recomendação Bayesiana
 * enquanto a infraestrutura definitiva de métricas e feedback não está completa.
 */
@Service
public class BayesianGatewayImpl implements BayesianGateway {

    private final QuestionGateway questionGateway;

    public BayesianGatewayImpl(QuestionGateway questionGateway) {
        this.questionGateway = questionGateway;
    }

    @Override
    public QuestionFeedbackSummary getFeedbackSummaryForQuestion(UUID questionId) {
        // Mock: Retorna dados neutros para não enviesar negativamente
        return new QuestionFeedbackSummary(
                questionId,
                0,
                0,
                0.0,
                0.0,
                new HashMap<>(),
                new HashMap<>());
    }

    @Override
    public List<UserRecruiter> getAllRecruiters() {
        return Collections.emptyList();
    }

    @Override
    public List<Question> getAllQuestions() {
        // Busca as 50 primeiras questões aprovadas para alimentar a sugestão
        return questionGateway.findAll(null, null, QuestionStatus.APPROVED, null, 0, 50);
    }
}
