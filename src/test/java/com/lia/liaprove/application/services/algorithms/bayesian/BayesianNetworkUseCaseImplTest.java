package com.lia.liaprove.application.services.algorithms.bayesian;

import com.lia.liaprove.application.gateways.algorithms.bayesian.BayesianGateway;
import com.lia.liaprove.core.algorithms.bayesian.BayesianConfig;
import com.lia.liaprove.core.algorithms.bayesian.BayesianVotingDecision;
import com.lia.liaprove.core.algorithms.bayesian.QuestionVoteSummary;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BayesianNetworkUseCaseImplTest {

    @Test
    void shouldApproveQuestionWhenProbabilityMeetsThresholdAndEvidenceMinimum() {
        UUID questionId = UUID.randomUUID();
        BayesianNetworkUseCaseImpl useCase = useCaseWithSummary(
                new QuestionVoteSummary(questionId, 5, 1, 5.0, 1.0)
        );

        BayesianVotingDecision decision = useCase.evaluateVotingResult(questionId);

        assertThat(decision.getResultStatus()).isEqualTo(QuestionStatus.APPROVED);
        assertThat(decision.getApprovalProbability()).isEqualTo(0.75);
        assertThat(decision.getEvidenceWeight()).isEqualTo(6.0);
        assertThat(decision.getApprovalThreshold()).isEqualTo(0.60);
    }

    @Test
    void shouldRejectQuestionWhenProbabilityIsBelowThreshold() {
        UUID questionId = UUID.randomUUID();
        BayesianNetworkUseCaseImpl useCase = useCaseWithSummary(
                new QuestionVoteSummary(questionId, 2, 4, 2.0, 4.0)
        );

        BayesianVotingDecision decision = useCase.evaluateVotingResult(questionId);

        assertThat(decision.getResultStatus()).isEqualTo(QuestionStatus.REJECTED);
        assertThat(decision.getApprovalProbability()).isEqualTo(0.375);
        assertThat(decision.getEvidenceWeight()).isEqualTo(6.0);
    }

    @Test
    void shouldRejectQuestionWhenEvidenceIsBelowMinimumEvenWithHighProbability() {
        UUID questionId = UUID.randomUUID();
        BayesianNetworkUseCaseImpl useCase = useCaseWithSummary(
                new QuestionVoteSummary(questionId, 1, 0, 1.0, 0.0)
        );

        BayesianVotingDecision decision = useCase.evaluateVotingResult(questionId);

        assertThat(decision.getResultStatus()).isEqualTo(QuestionStatus.REJECTED);
        assertThat(decision.getApprovalProbability()).isEqualTo(2.0 / 3.0);
        assertThat(decision.getEvidenceWeight()).isEqualTo(1.0);
    }

    @Test
    void shouldFallbackToSimpleVoteCountsWhenWeightedVotesAreEmpty() {
        UUID questionId = UUID.randomUUID();
        BayesianNetworkUseCaseImpl useCase = useCaseWithSummary(
                new QuestionVoteSummary(questionId, 4, 0, 0.0, 0.0)
        );

        BayesianVotingDecision decision = useCase.evaluateVotingResult(questionId);

        assertThat(decision.getResultStatus()).isEqualTo(QuestionStatus.APPROVED);
        assertThat(decision.getApprovalProbability()).isEqualTo(5.0 / 6.0);
        assertThat(decision.getEvidenceWeight()).isEqualTo(4.0);
    }

    private BayesianNetworkUseCaseImpl useCaseWithSummary(QuestionVoteSummary summary) {
        BayesianGateway gateway = new FixedSummaryBayesianGateway(summary);
        BayesianConfig config = new BayesianConfig(0.35, 0.30, 0.25, 0.10, 100, 1.0, 0.60, 3.0);
        return new BayesianNetworkUseCaseImpl(gateway, config);
    }

    private static class FixedSummaryBayesianGateway implements BayesianGateway {
        private final QuestionVoteSummary summary;

        private FixedSummaryBayesianGateway(QuestionVoteSummary summary) {
            this.summary = summary;
        }

        @Override
        public QuestionVoteSummary getVoteSummaryForQuestion(UUID questionId) {
            return summary;
        }

        @Override
        public List<UserRecruiter> getAllRecruiters() {
            return Collections.emptyList();
        }

        @Override
        public List<Question> getAllQuestions() {
            return Collections.emptyList();
        }
    }
}
