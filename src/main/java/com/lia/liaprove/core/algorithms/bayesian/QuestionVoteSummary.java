package com.lia.liaprove.core.algorithms.bayesian;

import java.util.Objects;
import java.util.UUID;

/**
 * Agregado com estatísticas dos votos de uma questao para o modelo Bayesiano.
 * Os votos ponderados devem ser calculados pela infraestrutura considerando o peso
 * do usuário e eventuais multiplicadores por papel antes de chegar ao core.
 */
public class QuestionVoteSummary {
    private final UUID questionId;
    private final int upCount;
    private final int downCount;
    private final double weightedUp;
    private final double weightedDown;

    public QuestionVoteSummary(UUID questionId, int upCount, int downCount, double weightedUp, double weightedDown) {
        if (upCount < 0 || downCount < 0) {
            throw new IllegalArgumentException("vote counts must be >= 0");
        }
        validateWeightedVote(weightedUp);
        validateWeightedVote(weightedDown);

        this.questionId = Objects.requireNonNull(questionId, "questionId must not be null");
        this.upCount = upCount;
        this.downCount = downCount;
        this.weightedUp = weightedUp;
        this.weightedDown = weightedDown;
    }

    private static void validateWeightedVote(double weightedVote) {
        if (!Double.isFinite(weightedVote) || weightedVote < 0.0) {
            throw new IllegalArgumentException("weighted votes must be finite and >= 0");
        }
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public int getUpCount() {
        return upCount;
    }

    public int getDownCount() {
        return downCount;
    }

    /**
     * Soma dos pesos efetivos dos votos positivos. A infraestrutura deve incluir
     * User.voteWeight e eventuais multiplicadores antes de preencher este valor.
     */
    public double getWeightedUp() {
        return weightedUp;
    }

    /**
     * Soma dos pesos efetivos dos votos negativos. A infraestrutura deve incluir
     * User.voteWeight e eventuais multiplicadores antes de preencher este valor.
     */
    public double getWeightedDown() {
        return weightedDown;
    }

    @Override
    public String toString() {
        return "QuestionVoteSummary{" +
                "questionId=" + questionId +
                ", upCount=" + upCount +
                ", downCount=" + downCount +
                ", weightedUp=" + weightedUp +
                ", weightedDown=" + weightedDown +
                '}';
    }
}
