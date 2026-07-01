package com.lia.liaprove.core.algorithms.bayesian;

/**
 * Parâmetros configuráveis do modelo de pontuação Bayesiana.
 * Mantém pesos e limites usados para normalização, ranking e suavização de Laplace.
 */
public final class BayesianConfig {
    private final double weightUsage;
    private final double weightRelevanceLLM;
    private final double weightUpvoteRatio;
    private final double weightRecruiter;
    private final int maxUsageForNormalization;
    private final double laplaceAlpha;

    public BayesianConfig(double weightUsage, double weightRelevanceLLM, double weightUpvoteRatio,
                          double weightRecruiter, int maxUsageForNormalization, double laplaceAlpha) {
        validateWeight(weightUsage);
        validateWeight(weightRelevanceLLM);
        validateWeight(weightUpvoteRatio);
        validateWeight(weightRecruiter);

        double sum = weightUsage + weightRelevanceLLM + weightUpvoteRatio + weightRecruiter;

        if (sum <= 0.0) throw new IllegalArgumentException("Bayesian weights must sum > 0");
        if (maxUsageForNormalization < 1) throw new IllegalArgumentException("maxUsageForNormalization must be >= 1");
        if (!Double.isFinite(laplaceAlpha) || laplaceAlpha <= 0.0) throw new IllegalArgumentException("laplaceAlpha must be finite and > 0");

        this.weightUsage = weightUsage;
        this.weightRelevanceLLM = weightRelevanceLLM;
        this.weightUpvoteRatio = weightUpvoteRatio;
        this.weightRecruiter = weightRecruiter;
        this.maxUsageForNormalization = maxUsageForNormalization;
        this.laplaceAlpha = laplaceAlpha;
    }

    private static void validateWeight(double weight) {
        if (!Double.isFinite(weight) || weight < 0.0) {
            throw new IllegalArgumentException("Bayesian weights must be finite and >= 0");
        }
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

    public double getLaplaceAlpha() {
        return laplaceAlpha;
    }

    public static BayesianConfig defaults() {
        return new BayesianConfig(0.35, 0.30, 0.25, 0.10, 100, 1.0);
    }

}
