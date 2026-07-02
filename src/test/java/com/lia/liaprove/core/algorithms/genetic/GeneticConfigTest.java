package com.lia.liaprove.core.algorithms.genetic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneticConfigTest {

    @Test
    @DisplayName("Should expose default GA parameters including total assessment normalizer")
    void shouldExposeDefaultParameters() {
        GeneticConfig config = GeneticConfig.defaults();

        assertThat(config.getPopulationSize()).isEqualTo(50);
        assertThat(config.getGenerations()).isEqualTo(30);
        assertThat(config.getMutationRate()).isEqualTo(0.05);
        assertThat(config.getCrossoverRate()).isEqualTo(0.7);
        assertThat(config.getMinWeight()).isEqualTo(1);
        assertThat(config.getMaxWeight()).isEqualTo(10);
        assertThat(config.getMaxRecentAssessments()).isEqualTo(10);
        assertThat(config.getMaxTotalAssessmentsCreated()).isEqualTo(100);
        assertThat(config.getMaxQuestionsApproved()).isEqualTo(20);
    }

    @Test
    @DisplayName("Should reject invalid total assessment normalizer")
    void shouldRejectInvalidTotalAssessmentNormalizer() {
        assertThatThrownBy(() -> new GeneticConfig(10, 5, 0.05, 0.7, 1, 10, 10, 0, 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("maxTotalAssessmentsCreated must be >= 1");
    }

    @Test
    @DisplayName("Should reject non finite mutation and crossover rates")
    void shouldRejectNonFiniteRates() {
        assertThatThrownBy(() -> new GeneticConfig(10, 5, Double.NaN, 0.7, 1, 10, 10, 100, 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("mutationRate must be finite and in [0,1]");

        assertThatThrownBy(() -> new GeneticConfig(10, 5, 0.05, Double.POSITIVE_INFINITY, 1, 10, 10, 100, 20))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("crossoverRate must be finite and in [0,1]");
    }
}
