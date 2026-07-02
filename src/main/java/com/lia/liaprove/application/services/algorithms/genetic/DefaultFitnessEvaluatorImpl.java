package com.lia.liaprove.application.services.algorithms.genetic;

import com.lia.liaprove.core.algorithms.genetic.FitnessEvaluator;
import com.lia.liaprove.core.algorithms.genetic.GeneticConfig;
import com.lia.liaprove.core.algorithms.genetic.Individual;
import com.lia.liaprove.core.algorithms.genetic.RecruiterMetrics;

import java.util.Objects;

/**
 * Avaliador de fitness que combina vários sinais:
 * - uso recente (recentAssessmentsCount)
 * - uso histórico (totalAssessmentsCreated)
 * - quantidade de questões aprovadas (questionsApprovedCount)
 * - reputação do recruiter (recruiterRating)
 * - peso atual (currentVoteWeight) para estabilidade
 *
 * Os sinais formam um alvo normalizado em [0..1]. A fitness mede quão perto o
 * gene candidato do indivíduo está desse alvo.
 *
 * Coeficientes podem ser ajustados via construtor.
 */
public class DefaultFitnessEvaluatorImpl implements FitnessEvaluator {
    private final GeneticConfig config;

    // Coeficientes (somas não precisam ser 1.0, pois normalizamos no final)
    private final double wRecentUsage;
    private final double wTotalUsage;
    private final double wQuestionsApproved;
    private final double wRecruiterReputation;
    private final double wCurrentWeight;

    public DefaultFitnessEvaluatorImpl(GeneticConfig config, double wRecentUsage, double wTotalUsage,
                                       double wQuestionsApproved, double wRecruiterReputation, double wCurrentWeight) {
        this.config = Objects.requireNonNull(config);
        this.wRecentUsage = wRecentUsage;
        this.wTotalUsage = wTotalUsage;
        this.wQuestionsApproved = wQuestionsApproved;
        this.wRecruiterReputation = wRecruiterReputation;
        this.wCurrentWeight = wCurrentWeight;
    }

    public static DefaultFitnessEvaluatorImpl defaultEvaluator(GeneticConfig config) {
        double wRecentUsage = 0.20;
        double wTotalUsage = 0.25;
        double wQuestionsApproved = 0.15;
        double wRecruiterReputation = 0.25;
        double wCurrentWeight = 0.15;

        return new DefaultFitnessEvaluatorImpl(
                config,
                wRecentUsage,
                wTotalUsage,
                wQuestionsApproved,
                wRecruiterReputation,
                wCurrentWeight
        );
    }

    @Override
    public double evaluate(Individual individual, RecruiterMetrics metrics) {
        if (individual == null || metrics == null) return 0.0;

        double recent = incentiveNormalize(metrics.getRecentAssessmentsCount(), config.getMaxRecentAssessments());
        double total = incentiveNormalize(metrics.getTotalAssessmentsCreated(), config.getMaxTotalAssessmentsCreated());
        double qApproved = incentiveNormalize(metrics.getQuestionsApprovedCount(), config.getMaxQuestionsApproved());
        double reputation = normalizeDouble(metrics.getRecruiterRating(), 0.0, 5.0);

        int current = metrics.getCurrentVoteWeight() == null ? config.getMinWeight() : metrics.getCurrentVoteWeight();
        double currentNorm = normalize(current, config.getMinWeight(), config.getMaxWeight());

        double target = weightedAverage(recent, total, qApproved, reputation, currentNorm);
        double distance = Math.abs(individual.getGene() - target);
        return clamp01(1.0 - distance);
    }

    // helpers

    private double weightedAverage(double recent, double total, double qApproved, double reputation, double currentNorm) {
        double s1 = positiveWeight(wRecentUsage);
        double s2 = positiveWeight(wTotalUsage);
        double s3 = positiveWeight(wQuestionsApproved);
        double s4 = positiveWeight(wRecruiterReputation);
        double s5 = positiveWeight(wCurrentWeight);
        double sum = s1 + s2 + s3 + s4 + s5;
        if (sum <= 0.0) return 0.5;
        return clamp01((s1 * recent + s2 * total + s3 * qApproved + s4 * reputation + s5 * currentNorm) / sum);
    }

    private double incentiveNormalize(int value, int max) {
        double normalized = normalize(value, 0, max);
        return 0.5 + 0.5 * normalized;
    }

    private double normalize(int v, int min, int max) {
        if (max <= min) return 0.0;
        int bounded = Math.clamp(v, min, max);
        return (double)(bounded - min) / (double)(max - min);
    }

    private double normalizeDouble(double v, double min, double max) {
        if (max <= min) return 0.0;
        double bounded = Math.clamp(v, min, max);
        return (bounded - min) / (max - min);
    }

    private double positiveWeight(double weight) {
        return Double.isFinite(weight) ? Math.max(0.0, weight) : 0.0;
    }

    private double clamp01(double x) {
        if (!Double.isFinite(x)) return 0.0;
        return Math.clamp(x, 0.0, 1.0);
    }

}
