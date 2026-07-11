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
    private final double approvalThreshold;
    private final double minimumVotingEvidenceWeight;

    public BayesianConfig(double weightUsage, double weightRelevanceLLM, double weightUpvoteRatio,
                          double weightRecruiter, int maxUsageForNormalization, double laplaceAlpha) {
        this(weightUsage, weightRelevanceLLM, weightUpvoteRatio, weightRecruiter, maxUsageForNormalization,
                laplaceAlpha, 0.60, 3.0);
    }

    public BayesianConfig(double weightUsage, double weightRelevanceLLM, double weightUpvoteRatio,
                          double weightRecruiter, int maxUsageForNormalization, double laplaceAlpha,
                          double approvalThreshold, double minimumVotingEvidenceWeight) {
        validateWeight(weightUsage);
        validateWeight(weightRelevanceLLM);
        validateWeight(weightUpvoteRatio);
        validateWeight(weightRecruiter);
        validateApprovalThreshold(approvalThreshold);
        validateMinimumVotingEvidenceWeight(minimumVotingEvidenceWeight);

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
        this.approvalThreshold = approvalThreshold;
        this.minimumVotingEvidenceWeight = minimumVotingEvidenceWeight;
    }

    private static void validateWeight(double weight) {
        if (!Double.isFinite(weight) || weight < 0.0) {
            throw new IllegalArgumentException("Bayesian weights must be finite and >= 0");
        }
    }

    private static void validateApprovalThreshold(double approvalThreshold) {
        if (!Double.isFinite(approvalThreshold) || approvalThreshold < 0.0 || approvalThreshold > 1.0) {
            throw new IllegalArgumentException("approvalThreshold must be finite and between 0.0 and 1.0");
        }
    }

    private static void validateMinimumVotingEvidenceWeight(double minimumVotingEvidenceWeight) {
        if (!Double.isFinite(minimumVotingEvidenceWeight) || minimumVotingEvidenceWeight < 0.0) {
            throw new IllegalArgumentException("minimumVotingEvidenceWeight must be finite and >= 0");
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

    public double getApprovalThreshold() {
        return approvalThreshold;
    }

    public double getMinimumVotingEvidenceWeight() {
        return minimumVotingEvidenceWeight;
    }

    public static BayesianConfig defaults() {
        return new BayesianConfig(0.35, 0.30, 0.25, 0.10, 100, 1.0, 0.60, 3.0);
    }

}
