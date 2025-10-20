package com.lia.liaprove.application.services.algorithms.genetic;

import com.lia.liaprove.core.algorithms.genetic.FitnessEvaluator;
import com.lia.liaprove.core.algorithms.genetic.GeneticConfig;
import com.lia.liaprove.core.algorithms.genetic.Individual;
import com.lia.liaprove.core.algorithms.genetic.RecruiterMetrics;

import java.util.Objects;

/**
 * Avaliador de fitness que combina vários sinais:
 * - uso recente (recentAssessmentsCount)
 * - média das avaliações de assessments (avgAssessmentRating)
 * - quantidade de questões aprovadas (questionsApprovedCount)
 * - razão likes em comentários (commentLikeRatio)
 * - peso atual (currentVoteWeight) para estabilidade
 *
 * Todos os termos são normalizados para [0..1] com base em GeneticConfig.
 *
 * Coeficientes podem ser ajustados via construtor.
 */
public class DefaultFitnessEvaluatorImpl implements FitnessEvaluator {
    private final GeneticConfig config;

    // Coeficientes (somas não precisam ser 1.0 pois normalizamos no final)
    private final double wRecentUsage;          // ex.: 0.3
    private final double wAvgAssessmentRating;  // ex.: 0.3
    private final double wQuestionsApproved;    // ex.: 0.2
    private final double wCommentLikeRatio;     // ex.: 0.1
    private final double wCurrentWeight;        // ex.: 0.1

    public DefaultFitnessEvaluatorImpl(GeneticConfig config, double wRecentUsage, double wAvgAssessmentRating,
                                       double wQuestionsApproved, double wCommentLikeRatio, double wCurrentWeight) {
        this.config = Objects.requireNonNull(config);
        this.wRecentUsage = wRecentUsage;
        this.wAvgAssessmentRating = wAvgAssessmentRating;
        this.wQuestionsApproved = wQuestionsApproved;
        this.wCommentLikeRatio = wCommentLikeRatio;
        this.wCurrentWeight = wCurrentWeight;
    }

    public static DefaultFitnessEvaluatorImpl defaultEvaluator(GeneticConfig config) {
        // Pesos iniciais (exemplo balanceado)
        double wRecentUsage = 0.25;         // uso recente (atividade)
        double wAvgAssessmentRating = 0.25; // qualidade das assessments
        double wQuestionsApproved = 0.20;   // histórico de questões aprovadas
        double wCommentLikeRatio = 0.20;    // qualidade de comentários (likes/dislikes)
        double wCurrentWeight = 0.10;       // estabilidade (peso atual)

        return new DefaultFitnessEvaluatorImpl(
                config,
                wRecentUsage,
                wAvgAssessmentRating,
                wQuestionsApproved,
                wCommentLikeRatio,
                wCurrentWeight
        );
    }

    @Override
    public double evaluate(Individual individual, RecruiterMetrics metrics) {
        if (metrics == null) return 0.0;

        // 1) recent usage (janela) -> normalized 0..1
        double recent = normalize(metrics.getRecentAssessmentsCount(), 0, config.getMaxRecentAssessments());

        // 2) avg assessment rating (0..5) -> 0..1
        double avgRating = normalizeDouble(metrics.getAvgAssessmentRating(), 0.0, 5.0);

        // 3) questions approved -> normalized
        double qApproved = normalize(metrics.getQuestionsApprovedCount(), 0, config.getMaxQuestionsApproved());

        // 4) comment like ratio (já 0..1)
        double likeRatio = metrics.getCommentLikeRatio();

        // 5) current weight normalized
        int current = metrics.getCurrentVoteWeight() == null ? config.getMinWeight() : metrics.getCurrentVoteWeight();
        double currentNorm = normalize(current, config.getMinWeight(), config.getMaxWeight());

        // combina pesos -> normaliza coeficientes para somar 1
        double s1 = Math.max(0.0, wRecentUsage);
        double s2 = Math.max(0.0, wAvgAssessmentRating);
        double s3 = Math.max(0.0, wQuestionsApproved);
        double s4 = Math.max(0.0, wCommentLikeRatio);
        double s5 = Math.max(0.0, wCurrentWeight);
        double sum = s1 + s2 + s3 + s4 + s5;
        if (sum <= 0.0) sum = 1.0;
        s1 /= sum; s2 /= sum; s3 /= sum; s4 /= sum; s5 /= sum;

        double raw = s1 * recent + s2 * avgRating + s3 * qApproved + s4 * likeRatio + s5 * currentNorm;
        return clamp01(raw);
    }

    // helpers

    private double normalize(int v, int min, int max) {
        if (max <= min) return 0.0;
        int bounded = Math.max(min, Math.min(max, v));
        return (double)(bounded - min) / (double)(max - min);
    }

    private double normalizeDouble(double v, double min, double max) {
        if (max <= min) return 0.0;
        double bounded = Math.max(min, Math.min(max, v));
        return (bounded - min) / (max - min);
    }

    private double clamp01(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return 0.0;
        return Math.max(0.0, Math.min(1.0, x));
    }

}
