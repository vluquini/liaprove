package com.lia.liaprove.core.algorithms.bayesian;

/**
 * Parâmetros configuráveis do motor Bayesiano.
 * Mantém pesos e limites que a implementação usa para normalização e ranking.
 * Inclusão do atributo laplaceAlpha para suavização de Laplace.
 */
public final class BayesianConfig {

    private final double weightUsage;        // ex.: 0.35
    private final double weightRelevanceLLM; // ex.: 0.30
    private final double weightUpvoteRatio;  // ex.: 0.25
    private final double weightRecruiter;    // ex.: 0.10

    private final int maxUsageForNormalization; // ex.: 100
    private final double maxRecruiterRating;    // ex.: 5.0

    // Laplace smoothing alpha (prior). Default = 1.0 (Laplace)
    private final double laplaceAlpha;

    public BayesianConfig(double weightUsage, double weightRelevanceLLM, double weightUpvoteRatio,
                          double weightRecruiter, int maxUsageForNormalization, double maxRecruiterRating, double laplaceAlpha) {
        double sum = weightUsage + weightRelevanceLLM + weightUpvoteRatio + weightRecruiter;

        if (sum <= 0.0) throw new IllegalArgumentException("Bayesian weights must sum > 0");
        if (maxUsageForNormalization < 1) throw new IllegalArgumentException("maxUsageForNormalization must be >= 1");
        if (maxRecruiterRating <= 0.0) throw new IllegalArgumentException("maxRecruiterRating must be > 0");
        if (laplaceAlpha <= 0.0) throw new IllegalArgumentException("laplaceAlpha must be > 0");

        this.weightUsage = weightUsage;
        this.weightRelevanceLLM = weightRelevanceLLM;
        this.weightUpvoteRatio = weightUpvoteRatio;
        this.weightRecruiter = weightRecruiter;
        this.maxUsageForNormalization = Math.max(1, maxUsageForNormalization);
        this.maxRecruiterRating = Math.max(0.1, maxRecruiterRating);
        this.laplaceAlpha = laplaceAlpha;
    }

    public double getWeightUsage() {
        return weightUsage;
    }

    public double getWeightRelevanceLLM() {
        return weightRelevanceLLM;
    }

    public double getWeightUpvoteRatio() {
        return weightUpvoteRatio;
    }

    public double getWeightRecruiter() {
        return weightRecruiter;
    }

    public int getMaxUsageForNormalization() {
        return maxUsageForNormalization;
    }

    public double getMaxRecruiterRating() {
        return maxRecruiterRating;
    }

    public double getLaplaceAlpha() {
        return laplaceAlpha;
    }

    // Factory with example defaults
    public static BayesianConfig defaults() {
        return new BayesianConfig(0.35, 0.30, 0.25, 0.10, 100, 5.0, 1.0);
    }

}
