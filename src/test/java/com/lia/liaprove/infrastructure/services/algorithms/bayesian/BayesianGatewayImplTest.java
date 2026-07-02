package com.lia.liaprove.infrastructure.services.algorithms.bayesian;

import com.lia.liaprove.application.gateways.algorithms.genetic.VoteMultiplierGateway;
import com.lia.liaprove.application.gateways.metrics.VoteGateway;
import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.algorithms.bayesian.QuestionVoteSummary;
import com.lia.liaprove.core.domain.metrics.QuestionVote;
import com.lia.liaprove.core.domain.metrics.VoteType;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.core.domain.user.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BayesianGatewayImplTest {

    @Test
    @DisplayName("Should aggregate weighted question votes using role and recruiter multipliers")
    void shouldAggregateWeightedVotesUsingMultipliers() {
        QuestionGateway questionGateway = mock(QuestionGateway.class);
        VoteGateway voteGateway = mock(VoteGateway.class);
        VoteMultiplierGateway multiplierGateway = mock(VoteMultiplierGateway.class);
        UUID questionId = UUID.randomUUID();
        UserRecruiter recruiter = recruiter(UUID.randomUUID(), 4);
        UserProfessional professional = professional(UUID.randomUUID(), 2);

        when(voteGateway.findVotesByQuestionId(questionId)).thenReturn(List.of(
                new QuestionVote(recruiter, question(questionId), VoteType.APPROVE),
                new QuestionVote(professional, question(questionId), VoteType.REJECT)
        ));
        when(multiplierGateway.getRecruiterMultiplier(recruiter.getId())).thenReturn(Optional.of(1.5));
        when(multiplierGateway.getRoleMultiplier(UserRole.PROFESSIONAL)).thenReturn(Optional.of(2.0));

        BayesianGatewayImpl gateway = new BayesianGatewayImpl(questionGateway, voteGateway, multiplierGateway);

        QuestionVoteSummary summary = gateway.getVoteSummaryForQuestion(questionId);

        assertThat(summary.getUpCount()).isEqualTo(1);
        assertThat(summary.getDownCount()).isEqualTo(1);
        assertThat(summary.getWeightedUp()).isEqualTo(6.0);
        assertThat(summary.getWeightedDown()).isEqualTo(4.0);
    }

    @Test
    @DisplayName("Should use multiplier one when no override exists")
    void shouldUseDefaultMultiplierOne() {
        QuestionGateway questionGateway = mock(QuestionGateway.class);
        VoteGateway voteGateway = mock(VoteGateway.class);
        VoteMultiplierGateway multiplierGateway = mock(VoteMultiplierGateway.class);
        UUID questionId = UUID.randomUUID();
        UserRecruiter recruiter = recruiter(UUID.randomUUID(), 3);

        when(voteGateway.findVotesByQuestionId(questionId)).thenReturn(List.of(
                new QuestionVote(recruiter, question(questionId), VoteType.APPROVE)
        ));
        when(multiplierGateway.getRecruiterMultiplier(recruiter.getId())).thenReturn(Optional.empty());
        when(multiplierGateway.getRoleMultiplier(UserRole.RECRUITER)).thenReturn(Optional.empty());

        BayesianGatewayImpl gateway = new BayesianGatewayImpl(questionGateway, voteGateway, multiplierGateway);

        QuestionVoteSummary summary = gateway.getVoteSummaryForQuestion(questionId);

        assertThat(summary.getUpCount()).isEqualTo(1);
        assertThat(summary.getDownCount()).isZero();
        assertThat(summary.getWeightedUp()).isEqualTo(3.0);
        assertThat(summary.getWeightedDown()).isZero();
    }

    private Question question(UUID questionId) {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion();
        question.setId(questionId);
        return question;
    }

    private UserRecruiter recruiter(UUID id, int voteWeight) {
        UserRecruiter recruiter = new UserRecruiter();
        recruiter.setId(id);
        recruiter.setRole(UserRole.RECRUITER);
        recruiter.setVoteWeight(voteWeight);
        return recruiter;
    }

    private UserProfessional professional(UUID id, int voteWeight) {
        UserProfessional professional = new UserProfessional();
        professional.setId(id);
        professional.setRole(UserRole.PROFESSIONAL);
        professional.setVoteWeight(voteWeight);
        return professional;
    }
}
