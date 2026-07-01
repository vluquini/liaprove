package com.lia.liaprove.core.algorithms.bayesian;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuestionVoteSummaryTest {

    @Test
    void shouldExposeVoteCountsAndWeightedVotes() {
        UUID questionId = UUID.randomUUID();

        QuestionVoteSummary summary = new QuestionVoteSummary(questionId, 3, 1, 7.5, 2.0);

        assertThat(summary.getQuestionId()).isEqualTo(questionId);
        assertThat(summary.getUpCount()).isEqualTo(3);
        assertThat(summary.getDownCount()).isEqualTo(1);
        assertThat(summary.getWeightedUp()).isEqualTo(7.5);
        assertThat(summary.getWeightedDown()).isEqualTo(2.0);
    }

    @Test
    void shouldRejectNullQuestionId() {
        assertThatThrownBy(() -> new QuestionVoteSummary(null, 0, 0, 0.0, 0.0))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("questionId must not be null");
    }

    @Test
    void shouldRejectNegativeCounts() {
        UUID questionId = UUID.randomUUID();

        assertThatThrownBy(() -> new QuestionVoteSummary(questionId, -1, 0, 0.0, 0.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("vote counts must be >= 0");
    }

    @Test
    void shouldRejectInvalidWeightedVotes() {
        UUID questionId = UUID.randomUUID();

        assertThatThrownBy(() -> new QuestionVoteSummary(questionId, 0, 0, Double.NaN, 0.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("weighted votes must be finite and >= 0");
    }
}
