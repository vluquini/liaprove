package com.lia.liaprove.infrastructure.services.algorithms.bayesian;

import com.lia.liaprove.application.gateways.algorithms.bayesian.BayesianGateway;
import com.lia.liaprove.application.gateways.algorithms.genetic.VoteMultiplierGateway;
import com.lia.liaprove.application.gateways.metrics.VoteGateway;
import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.algorithms.bayesian.QuestionVoteSummary;
import com.lia.liaprove.core.domain.metrics.QuestionVote;
import com.lia.liaprove.core.domain.metrics.VoteType;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação TEMPORÁRIA do BayesianGateway.
 * Esta classe serve para viabilizar a execução do motor de recomendação Bayesiana
 * enquanto a infraestrutura definitiva de métricas e feedback não está completa.
 */
@Service
public class BayesianGatewayImpl implements BayesianGateway {

    private final QuestionGateway questionGateway;
    private final VoteGateway voteGateway;
    private final VoteMultiplierGateway voteMultiplierGateway;

    public BayesianGatewayImpl(QuestionGateway questionGateway, VoteGateway voteGateway,
                               VoteMultiplierGateway voteMultiplierGateway) {
        this.questionGateway = Objects.requireNonNull(questionGateway);
        this.voteGateway = Objects.requireNonNull(voteGateway);
        this.voteMultiplierGateway = Objects.requireNonNull(voteMultiplierGateway);
    }

    @Override
    public QuestionVoteSummary getVoteSummaryForQuestion(UUID questionId) {
        List<QuestionVote> votes = voteGateway.findVotesByQuestionId(questionId);
        if (votes == null || votes.isEmpty()) {
            return new QuestionVoteSummary(questionId, 0, 0, 0.0, 0.0);
        }

        int upCount = 0;
        int downCount = 0;
        double weightedUp = 0.0;
        double weightedDown = 0.0;

        for (QuestionVote vote : votes) {
            if (vote == null || vote.getVoteType() == null) {
                continue;
            }

            double weight = effectiveWeight(vote.getUser());
            if (vote.getVoteType() == VoteType.APPROVE) {
                upCount++;
                weightedUp += weight;
            } else if (vote.getVoteType() == VoteType.REJECT) {
                downCount++;
                weightedDown += weight;
            }
        }

        return new QuestionVoteSummary(questionId, upCount, downCount, weightedUp, weightedDown);
    }

    @Override
    public List<UserRecruiter> getAllRecruiters() {
        return Collections.emptyList();
    }

    @Override
    public List<Question> getAllQuestions() {
        // Busca um pool suficiente de questões disponíveis antes dos filtros de sugestão.
        return questionGateway.findAll(null, null, QuestionStatus.FINISHED, null, null, 0, 200);
    }

    private double effectiveWeight(User user) {
        if (user == null) {
            return 1.0;
        }
        int voteWeight = user.getVoteWeight() == null ? 1 : Math.max(1, user.getVoteWeight());
        return voteWeight * effectiveMultiplier(user);
    }

    private double effectiveMultiplier(User user) {
        if (user instanceof UserRecruiter recruiter && recruiter.getId() != null) {
            Optional<Double> override = voteMultiplierGateway.getRecruiterMultiplier(recruiter.getId());
            if (override != null && override.isPresent()) {
                return sanitizeMultiplier(override.get());
            }
        }
        if (user.getRole() == null) {
            return 1.0;
        }

        Optional<Double> roleMultiplier = voteMultiplierGateway.getRoleMultiplier(user.getRole());
        return roleMultiplier.map(this::sanitizeMultiplier).orElse(1.0);
    }

    private double sanitizeMultiplier(double multiplier) {
        if (!Double.isFinite(multiplier) || multiplier < 0.0) {
            return 1.0;
        }
        return multiplier;
    }
}
