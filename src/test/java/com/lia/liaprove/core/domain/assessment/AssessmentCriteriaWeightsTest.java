package com.lia.liaprove.core.domain.assessment;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssessmentCriteriaWeightsTest {

    @Test
    void shouldCreateDefaultWeights() {
        AssessmentCriteriaWeights weights = AssessmentCriteriaWeights.defaultWeights();

        assertThat(weights.getHardSkillsWeight()).isEqualTo(34);
        assertThat(weights.getSoftSkillsWeight()).isEqualTo(33);
        assertThat(weights.getExperienceWeight()).isEqualTo(33);
    }

    @Test
    void shouldAcceptWeightsThatSumToOneHundred() {
        AssessmentCriteriaWeights weights = new AssessmentCriteriaWeights(50, 30, 20);

        assertThat(weights.getHardSkillsWeight()).isEqualTo(50);
        assertThat(weights.getSoftSkillsWeight()).isEqualTo(30);
        assertThat(weights.getExperienceWeight()).isEqualTo(20);
    }

    @Test
    void shouldRejectNegativeWeights() {
        assertThatThrownBy(() -> new AssessmentCriteriaWeights(-1, 80, 21))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Criteria weights must be non-negative.");

        assertThatThrownBy(() -> new AssessmentCriteriaWeights(80, -1, 21))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Criteria weights must be non-negative.");

        assertThatThrownBy(() -> new AssessmentCriteriaWeights(80, 21, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Criteria weights must be non-negative.");
    }

    @Test
    void shouldRejectWeightsThatDoNotSumToOneHundred() {
        assertThatThrownBy(() -> new AssessmentCriteriaWeights(50, 30, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Criteria weights must sum to 100.");
    }

    @Test
    void shouldCompareByValue() {
        assertThat(new AssessmentCriteriaWeights(50, 30, 20))
                .isEqualTo(new AssessmentCriteriaWeights(50, 30, 20))
                .hasSameHashCodeAs(new AssessmentCriteriaWeights(50, 30, 20));
    }

    @Test
    void shouldNotEqualDifferentValuesOrTypes() {
        AssessmentCriteriaWeights weights = new AssessmentCriteriaWeights(50, 30, 20);

        assertThat(weights.equals(weights)).isTrue();
        assertThat(weights.equals(null)).isFalse();
        assertThat(weights.equals("weights")).isFalse();
        assertThat(weights).isNotEqualTo(new AssessmentCriteriaWeights(40, 40, 20));
        assertThat(weights).isNotEqualTo(new AssessmentCriteriaWeights(50, 20, 30));
        assertThat(weights).isNotEqualTo(new AssessmentCriteriaWeights(50, 40, 10));
    }
}
