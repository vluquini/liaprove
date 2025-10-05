package com.lia.liaprove.application.services.algorithms.bayesian;

import com.lia.liaprove.core.algorithms.bayesian.BayesianConfig;
import com.lia.liaprove.application.gateways.algorithms.bayesian.BayesianGateway;
import com.lia.liaprove.core.algorithms.bayesian.QuestionFeedbackSummary;
import com.lia.liaprove.core.algorithms.bayesian.ScoredQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.core.usecases.algorithms.bayesian.BayesianNetworkUseCase;

import java.util.*;
import java.util.stream.Collectors;
/**
 * Implementação simples de um "motor Bayesiano" — aqui usamos heurísticas e agregações,
 * não uma biblioteca completa de Bayes. Serve como prova de conceito (POC) e poderá ser substituível.
 *
 * Fórmula de exemplo (heurística):
 * score = w1 * normalized_recruiterUsageCount
 *       + w2 * normalized_relevanceByLLM
 *       + w3 * normalized_upvote_ratio
 *       + w4 * normalized_recruiterEngagementScore
 *
 * Campos do domínio:
 * - Question.getRecruiterUsageCount()  -> int
 * - Question.getRelevanceByLLM()       -> RelevanceLevel type 1..5
 * - Question.getUpVote()/getDownVote() -> Vote type
 * - UserRecruiter.getRecruiterRating() -> Float
 *
 *  * Observações:
 *  * - Usa QuestionFeedbackSummary (agregação) via provider para obter up/down sem varrer listas.
 *  * - Constrói um mapa de recruiters uma vez por operação (melhora performance).
 *  * - Normaliza corretamente RelevanceLevel (1..5 -> 0..1).
 */
public class BayesianNetworkUseCaseImpl implements BayesianNetworkUseCase {
    private final BayesianGateway provider;
    private final BayesianConfig config;

    public BayesianNetworkUseCaseImpl(BayesianGateway provider, BayesianConfig config) {
        this.provider = Objects.requireNonNull(provider, "provider must not be null");
        this.config = Objects.requireNonNull(config, "config must not be null");
    }

    @Override
    public double probabilityQuestionApproved(Question q, UUID recruiterId) {
        // Recupera um Map de recrutadores uma única vez
        Map<UUID, UserRecruiter> recruiterMap = provider.getAllRecruiters()
                .stream()
                .collect(Collectors.toMap(UserRecruiter::getId, r -> r));

        return probabilityQuestionApprovedInternal(q, recruiterId, recruiterMap);
    }

    // Reutiliza um mapa de recrutador para desempenho ao avaliar muitas perguntas
    private double probabilityQuestionApprovedInternal(Question q, UUID recruiterId, Map<UUID, UserRecruiter> recruiterMap) {
        // 1) Feedback Summary (agregado)
        QuestionFeedbackSummary summary = provider.getFeedbackSummaryForQuestion(q.getId());

        // Valores ponderados fornecidos pela infra
        double weightedUp = summary == null ? 0.0 : summary.getWeightedUp();
        double weightedDown = summary == null ? 0.0 : summary.getWeightedDown();

        // Se infra não fornecer pesos (ambos zero), usamos contagens simples como fallback
        if (weightedUp == 0.0 && weightedDown == 0.0 && summary != null) {
            weightedUp = summary.getUpCount();
            weightedDown = summary.getDownCount();
        }
        // 2) Fórmula simples de proporção amostral + Laplace smoothing (alpha configurável); fallback neutro
        double alpha = config.getLaplaceAlpha();
        double upRatio = (weightedUp + alpha) / (weightedUp + weightedDown + 2.0 * alpha);

        // 3) Relevance by LLM (enum -> normalized 0..1)
        RelevanceLevel relEnum = q.getRelevanceByLLM();
        double normalizedRelByLLM = relEnum == null ? 0.0 : ((double)(relEnum.getRelevanceLevel() - 1) / 4.0);

        // 4) Recruiter engagement score
        UserRecruiter recruiter = recruiterMap.get(recruiterId);
        double normalizedRecEngagement = recruiter == null ? 0.0 : safeClamp(recruiter.getRecruiterEngagementScore(), 0.0, 1.0);

        // 5) Question usage (contador por questão)
        int usageRaw = safeInt(q.getRecruiterUsageCount());
        double normalizedUsage = normalize(usageRaw, 0, config.getMaxUsageForNormalization());

        // 6) Combinação ponderada (normaliza pesos do config para somarem 1)
        double wUsage = config.getWeightUsage();
        double wRel   = config.getWeightRelevanceLLM();
        double wUp    = config.getWeightUpvoteRatio();
        double wRec   = config.getWeightRecruiter();
        double sum    = wUsage + wRel + wUp + wRec;

        if (sum <= 0.0) sum = 1.0;
        wUsage /= sum; wRel /= sum; wUp /= sum; wRec /= sum;

        double score = wUsage * normalizedUsage
                + wRel   * normalizedRelByLLM
                + wUp    * upRatio
                + wRec   * normalizedRecEngagement;

        return Math.max(0.0, Math.min(1.0, score));
    }

    @Override
    public List<ScoredQuestion> suggestQuestionsForRecruiter(UUID recruiterId, int limit) {
        Objects.requireNonNull(recruiterId, "recruiterId must not be null");
        if (limit <= 0) limit = 10;

        // Recupera um Map de recrutadores uma única vez
        Map<UUID, UserRecruiter> recruiterMap = provider.getAllRecruiters()
                .stream()
                .collect(Collectors.toMap(UserRecruiter::getId, r -> r));

        List<Question> all = provider.getAllQuestions();
        if (all == null || all.isEmpty()) return Collections.emptyList();

        List<ScoredQuestion> scored = new ArrayList<>(all.size());
        for (Question q : all) {
            double score = probabilityQuestionApprovedInternal(q, recruiterId, recruiterMap);
            scored.add(new ScoredQuestion(q, score));
        }

        scored.sort(Comparator.comparingDouble(ScoredQuestion::getScore).reversed());
        return scored.stream().limit(limit).collect(Collectors.toList());
    }

    // -----------------------
    // Util helpers
    // -----------------------
    private static int safeInt(Integer v) {
        return v == null ? 0 : v;
    }

    private static double normalize(int value, int min, int max) {
        if (max <= min) return 0.0;
        int bounded = Math.max(min, Math.min(max, value));
        return (double) (bounded - min) / (double) (max - min);
    }

    private static double safeClamp(double v, double min, double max) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return min;
        return Math.max(min, Math.min(max, v));
    }
}
