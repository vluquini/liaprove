package com.lia.liaprove.core.algorithms.genetic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecruiterMetricsTest {

    @Test
    @DisplayName("Should sanitize counters and clamp ratings")
    void shouldSanitizeCountersAndClampRatings() {
        RecruiterMetrics metrics = new RecruiterMetrics(
                UUID.randomUUID(),
                null,
                -5,
                -1,
                7.5,
                -2,
                -3,
                -4,
                Double.NaN,
                8.0
        );

        assertThat(metrics.getTotalAssessmentsCreated()).isZero();
        assertThat(metrics.getRecentAssessmentsCount()).isZero();
        assertThat(metrics.getAvgAssessmentRating()).isEqualTo(5.0);
        assertThat(metrics.getQuestionsApprovedCount()).isZero();
        assertThat(metrics.getFeedbackLikes()).isZero();
        assertThat(metrics.getFeedbackDislikes()).isZero();
        assertThat(metrics.getCommentLikeRatio()).isEqualTo(0.5);
        assertThat(metrics.getRecruiterRating()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Should compute neutral ratio when feedback has no reactions")
    void shouldComputeNeutralRatioWhenFeedbackHasNoReactions() {
        RecruiterMetrics metrics = new RecruiterMetrics(
                UUID.randomUUID(),
                4,
                0,
                0,
                0.0,
                0,
                0,
                0,
                Double.NaN,
                2.5
        );

        assertThat(metrics.getCommentLikeRatio()).isEqualTo(0.5);
    }

    @Test
    @DisplayName("Should reject null recruiter id")
    void shouldRejectNullRecruiterId() {
        assertThatThrownBy(() -> new RecruiterMetrics(null, null, 0, 0, 0.0, 0, 0, 0, 0.5, 2.5))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("recruiterId");
    }
}
