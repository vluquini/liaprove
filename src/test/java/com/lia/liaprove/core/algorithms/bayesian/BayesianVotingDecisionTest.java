package com.lia.liaprove.core.algorithms.bayesian;

import com.lia.liaprove.core.domain.question.QuestionStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BayesianVotingDecisionTest {

    @Test
    void shouldExposeVotingDecisionData() {
        UUID questionId = UUID.randomUUID();

        BayesianVotingDecision decision = new BayesianVotingDecision(
                questionId,
                0.72,
                QuestionStatus.APPROVED,
                8.0,
                0.60
        );

        assertThat(decision.getQuestionId()).isEqualTo(questionId);
        assertThat(decision.getApprovalProbability()).isEqualTo(0.72);
        assertThat(decision.getResultStatus()).isEqualTo(QuestionStatus.APPROVED);
        assertThat(decision.getEvidenceWeight()).isEqualTo(8.0);
        assertThat(decision.getApprovalThreshold()).isEqualTo(0.60);
        assertThat(decision.isApproved()).isTrue();
    }

    @Test
    void shouldRejectInvalidProbability() {
        assertThatThrownBy(() -> new BayesianVotingDecision(
                UUID.randomUUID(),
                1.1,
                QuestionStatus.APPROVED,
                1.0,
                0.60
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("approvalProbability must be finite and between 0.0 and 1.0");
    }

    @Test
    void shouldRejectNonVotingResultStatuses() {
        assertThatThrownBy(() -> new BayesianVotingDecision(
                UUID.randomUUID(),
                0.8,
                QuestionStatus.FINISHED,
                5.0,
                0.60
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("resultStatus must be APPROVED or REJECTED");
    }
}
