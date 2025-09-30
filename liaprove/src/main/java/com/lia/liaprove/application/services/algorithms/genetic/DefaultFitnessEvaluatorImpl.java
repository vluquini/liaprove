package com.lia.liaprove.application.services.algorithms.genetic;

import com.lia.liaprove.core.algorithms.genetic.FitnessEvaluator;
import com.lia.liaprove.core.algorithms.genetic.GeneticConfig;
import com.lia.liaprove.core.algorithms.genetic.Individual;
import com.lia.liaprove.core.domain.user.UserRecruiter;

import java.util.Objects;

/**
 * Avaliador de fitness que usa os 3 coeficientes do GeneticConfig:
 *   - alphaUsage  : peso para totalAssessmentsCreated (uso)
 *   - betaRating  : peso para recruiterRating
 *   - gammaWeight : peso para voteWeight (peso atual do voto)
 *
 * Normalizações:
 *  - usage é normalizado por maxExpectedAssessments (passado no construtor)
 *  - rating é normalizado por MAX_RATING (0..5)
 *  - weight é normalizado por config.minWeight..config.maxWeight
 *
 * Resultado: valor em 0..1
 */
public class DefaultFitnessEvaluatorImpl implements FitnessEvaluator {

    private static final double MAX_RATING = 5.0;

    private final GeneticConfig config;
    private final int maxExpectedAssessments; // para normalizar uso (ex.: 100)

    public DefaultFitnessEvaluatorImpl(GeneticConfig config, int maxExpectedAssessments) {
        this.config = Objects.requireNonNull(config, "config cannot be null");
        if (maxExpectedAssessments <= 0) throw new IllegalArgumentException("maxExpectedAssessments must be > 0");
        this.maxExpectedAssessments = maxExpectedAssessments;
    }

    @Override
    public double evaluate(Individual individual, UserRecruiter recruiter) {
        if (recruiter == null) return 0.0;

        // 1) usage (totalAssessmentsCreated) normalized 0..1
        int usageRaw = recruiter.getTotalAssessmentsCreated() == null ? 0 : recruiter.getTotalAssessmentsCreated();
        double normalizedUsage = Math.min(1.0, (double) usageRaw / (double) maxExpectedAssessments);

        // 2) rating normalized 0..1
        double ratingRaw = recruiter.getRecruiterRating() == null ? 0.0 : recruiter.getRecruiterRating();
        double normalizedRating = Math.min(1.0, ratingRaw / MAX_RATING);

        // 3) current weight normalized 0..1 using config bounds
        int weightRaw = recruiter.getVoteWeight() == null ? 0 : recruiter.getVoteWeight();
        double normalizedWeight = normalizeInt(weightRaw, config.getMinWeight(), config.getMaxWeight());

        // combine using config coefficients
        double a = config.getAlphaUsage();
        double b = config.getBetaRating();
        double g = config.getGammaCurrent();

        // normalize coefficients so they sum to 1 (keeps relative importance even if config was scaled)
        double sum = a + b + g;
        if (sum <= 0.0) sum = 1.0;
        a /= sum; b /= sum; g /= sum;

        double rawScore = a * normalizedUsage + b * normalizedRating + g * normalizedWeight;

        // clamp to [0,1]
        return Math.max(0.0, Math.min(1.0, rawScore));
    }

    private double normalizeInt(int v, int min, int max) {
        if (max <= min) return 0.0;
        int bounded = Math.max(min, Math.min(max, v));
        return (double) (bounded - min) / (double) (max - min);
    }
}
