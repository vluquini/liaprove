package com.lia.liaprove.core.algorithms.bayesian;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BayesianConfigTest {

    @Test
    void shouldCreateDefaultConfigForBayesianScoringModel() {
        BayesianConfig config = BayesianConfig.defaults();

        assertThat(config.getWeightUsage()).isEqualTo(0.35);
        assertThat(config.getWeightRelevanceLLM()).isEqualTo(0.30);
        assertThat(config.getWeightUpvoteRatio()).isEqualTo(0.25);
        assertThat(config.getWeightRecruiter()).isEqualTo(0.10);
        assertThat(config.getMaxUsageForNormalization()).isEqualTo(100);
        assertThat(config.getLaplaceAlpha()).isEqualTo(1.0);
        assertThat(config.getApprovalThreshold()).isEqualTo(0.60);
        assertThat(config.getMinimumVotingEvidenceWeight()).isEqualTo(3.0);
    }

    @Test
    void shouldRejectNegativeWeightsEvenWhenWeightSumIsPositive() {
        assertThatThrownBy(() -> new BayesianConfig(-0.1, 0.5, 0.4, 0.2, 100, 1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bayesian weights must be finite and >= 0");
    }

    @Test
    void shouldRejectZeroWeightSum() {
        assertThatThrownBy(() -> new BayesianConfig(0.0, 0.0, 0.0, 0.0, 100, 1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bayesian weights must sum > 0");
    }

    @Test
    void shouldRejectInvalidApprovalThreshold() {
        assertThatThrownBy(() -> new BayesianConfig(0.35, 0.30, 0.25, 0.10, 100, 1.0, 1.1, 3.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("approvalThreshold must be finite and between 0.0 and 1.0");
    }

    @Test
    void shouldRejectInvalidMinimumVotingEvidenceWeight() {
        assertThatThrownBy(() -> new BayesianConfig(0.35, 0.30, 0.25, 0.10, 100, 1.0, 0.60, -1.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minimumVotingEvidenceWeight must be finite and >= 0");
    }
}
