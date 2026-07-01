package com.lia.liaprove.application.services.algorithms.bayesian;

import com.lia.liaprove.core.algorithms.bayesian.BayesianConfig;
import com.lia.liaprove.application.gateways.algorithms.bayesian.BayesianGateway;
import com.lia.liaprove.core.algorithms.bayesian.QuestionVoteSummary;
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
 * Responsabilidades:
 * - Agregar sinais (feedbacks, uso da questão, relevância fornecida pela LLM, métricas do recruiter)
 *   e calcular uma pontuação normalizada [0..1] para recomendar uma questão a um recruiter.
 * - Sugerir questões ordenadas por essa pontuação para um recruiter.
 *
 * Fórmula de exemplo (heurística):
 * score = w1 * normalized_recruiterUsageCount
 *       + w2 * normalized_relevanceByLLM
 *       + w3 * normalized_upvote_ratio
 *       + w4 * normalized_recruiterEngagementScore
 *
 * Campos do domínio:
 * - Question.getRecruiterUsageCount()                         -> int
 * - Question.getRelevanceByLLM()                              -> RelevanceLevel type 1..5
 * - QuestionVoteSummary.getWeightedUp()/getWeightedDown() -> int
 * - UserRecruiter.getRecruiterRating()                        -> float
 *
 * Observações:
 * - A agregação de votos (pesos por usuário) deve ser feita preferencialmente pela camada de infra
 *   via {@code BayesianGateway#getVoteSummaryForQuestion(...)} para evitar varreduras desnecessárias.
 * - Constrói um mapa de recruiters uma vez por operação de sugestão (melhora performance).
 * - Normaliza corretamente RelevanceLevel (1..5 -> 0..1).
 */
public class BayesianNetworkUseCaseImpl implements BayesianNetworkUseCase {
    private final BayesianGateway provider;
    private final BayesianConfig config;

    public BayesianNetworkUseCaseImpl(BayesianGateway provider, BayesianConfig config) {
        this.provider = Objects.requireNonNull(provider, "provider must not be null");
        this.config = Objects.requireNonNull(config, "config must not be null");
    }

    /**
     * Implementação interna do cálculo de score para uma questão.
     *
     * Passos principais:
     * 1) obtém resumo de votos (QuestionVoteSummary) via gateway;
     * 2) computa razão de up-votes com smoothing (Laplace);
     * 3) normaliza campos (relevância LLM, uso da questão, engagement do recruiter);
     * 4) combina os sinais com pesos definidos em {@code BayesianConfig} e retorna o score.
     *
     * @param q           questão avaliada
     * @param recruiterId id do recruiter para contexto
     * @param recruiterMap mapa id->UserRecruiter para lookup (pode ser vazio)
     * @return score no intervalo [0.0,1.0]
     */
    private double scoreQuestionForRecruiter(Question q, UUID recruiterId, Map<UUID, UserRecruiter> recruiterMap) {
        QuestionVoteSummary summary = provider.getVoteSummaryForQuestion(q.getId());

        // Valores ponderados fornecidos pela infra
        double upRatio = computeUpVoteRatio(summary);

        // 3) Relevance by LLM (enum -> normalized 0..1)
        RelevanceLevel relEnum = q.getRelevanceByLLM();
        double normalizedRelByLLM = relEnum == null ? 0.0 : ((double)(relEnum.getRelevanceLevel() - 1) / 4.0);

        // 4) Recruiter engagement score
        UserRecruiter recruiter = recruiterMap.get(recruiterId);
        double normalizedRecEngagement = recruiter == null ? 0.0 : safeClamp(recruiter.getRecruiterEngagementScore(), 0.0, 1.0);

        // 5) Question usage (contador por questão)
        int usageRaw = q.getRecruiterUsageCount();
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

        return Math.clamp(score, 0.0, 1.0);
    }

    /**
     * Calcula a razão de up-votes com Laplace smoothing.
     *
     * Estratégia:
     * - usa os pesos ponderados fornecidos pela infra (summary.getWeightedUp()/getWeightedDown()) quando disponíveis;
     * - se a infra não fornecer pesos (ambos zero) faz fallback para contagens simples (upCount/downCount);
     * - aplica Laplace smoothing com alpha vindo de config: (up + alpha) / (up + down + 2*alpha)
     * - se denom <= 0 (situação anômala) retorna 0.5 como neutro.
     *
     * @param summary resumo de votos (pode ser null)
     * @return upRatio no intervalo [0.0, 1.0]
     */
    private double computeUpVoteRatio(QuestionVoteSummary summary) {
        double weightedUp = summary == null ? 0.0 : summary.getWeightedUp();
        double weightedDown = summary == null ? 0.0 : summary.getWeightedDown();

        // Se infra não fornecer pesos (ambos zero), usamos contagens simples como fallback
        if (weightedUp == 0.0 && weightedDown == 0.0 && summary != null) {
            weightedUp = summary.getUpCount();
            weightedDown = summary.getDownCount();
        }

        // 2) Fórmula simples de proporção amostral + Laplace smoothing (alpha configurável); fallback neutro
        double alpha = Math.max(0.0, config.getLaplaceAlpha());
        double denom = weightedUp + weightedDown + 2.0 * alpha;

        if (denom <= 0.0) return 0.5; // fallback neutro (nao deve ocorrer se alpha > 0)
        return (weightedUp + alpha) / denom;
    }

    /**
     * Sugere questões para um recruiter, ordenadas pela probabilidade calculada pela rede bayesiana.
     *
     * Implementação atual:
     * - percorre todas as questões candidatas, calcula o score
     *   e retorna as top {@code limit} ordenadas.
     *
     * @param recruiterId id do recruiter que receberá as sugestões (não nulo)
     * @param limit       número máximo de questões a retornar (se <=0, valor padrão é usado)
     * @return lista de {@link ScoredQuestion} ordenada por score descendente
     */
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
            double score = scoreQuestionForRecruiter(q, recruiterId, recruiterMap);
            scored.add(new ScoredQuestion(q, score));
        }

        scored.sort(Comparator.comparingDouble(ScoredQuestion::getScore).reversed());
        return scored.stream().limit(limit).collect(Collectors.toList());
    }

    // helpers

    /**
     * Normaliza um inteiro no intervalo [min, max] para [0.0, 1.0] usando min-max.
     * Se {@code max <= min} retorna 0.0.
     *
     * @param value valor a normalizar
     * @param min   mínimo esperado
     * @param max   máximo esperado
     * @return valor normalizado 0..1
     */
    private static double normalize(int value, int min, int max) {
        if (max <= min) return 0.0;
        int bounded = Math.clamp(value, min, max);
        return (double) (bounded - min) / (double) (max - min);
    }

    /**
     * Faz clamp (satura) `v` entre `min` e `max`. Em caso de NaN/Infinite retorna `min`.
     *
     * @param v valor a clamar
     * @param min mínimo
     * @param max máximo
     * @return valor satureado entre min e max
     */
    private static double safeClamp(double v, double min, double max) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return min;
        return Math.clamp(v, min, max);
    }
}
