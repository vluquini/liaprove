package com.lia.liaprove.core.algorithms.bayesian;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.RelevanceLevel;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * Agregado com estatísticas de feedback por questão.
 * Fornece as contagens que o motor bayesiano precisa (em vez de transferir listas inteiras).
 */
public class QuestionFeedbackSummary {
    private final UUID questionId;
    private final int upCount;
    private final int downCount;
    // Somas ponderadas (calculadas na infra; se não existirem, serão 0.0)
    private final double weightedUp;
    private final double weightedDown;
    private final Map<RelevanceLevel, Integer> relevanceCounts;
    private final Map<DifficultyLevel, Integer> difficultyCounts;
    private final int totalFeedbacks;

    public QuestionFeedbackSummary(UUID questionId, int upCount, int downCount, double weightedUp, double weightedDown,
                                   Map<RelevanceLevel, Integer> relevanceCounts, Map<DifficultyLevel, Integer> difficultyCounts) {
        this.questionId = questionId;
        this.upCount = upCount;
        this.downCount = downCount;
        this.weightedUp = weightedUp;
        this.weightedDown = weightedDown;
        this.relevanceCounts = relevanceCounts == null ? Collections.emptyMap() : Collections.unmodifiableMap(relevanceCounts);
        this.difficultyCounts = difficultyCounts == null ? Collections.emptyMap() : Collections.unmodifiableMap(difficultyCounts);
        this.totalFeedbacks = upCount + downCount;
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
     * Soma dos pesos dos votos positivos (ex.: Σ voteWeight * roleMultiplier para votos UP).
     * Se a infra não calcular pesos, este campo pode ser 0.0.
     */
    public double getWeightedUp() {
        return weightedUp;
    }

    /**
     * Soma dos pesos dos votos negativos (ex.: Σ voteWeight * roleMultiplier para votos DOWN).
     * Se a infra não calcular pesos, este campo pode ser 0.0.
     */
    public double getWeightedDown() {
        return weightedDown;
    }

    public Map<RelevanceLevel, Integer> getRelevanceCounts() {
        return relevanceCounts;
    }

    public Map<DifficultyLevel, Integer> getDifficultyCounts() {
        return difficultyCounts;
    }

    public int getTotalFeedbacks() {
        return totalFeedbacks;
    }

    @Override
    public String toString() {
        return "QuestionFeedbackSummary{" +
                "questionId=" + questionId +
                ", upCount=" + upCount +
                ", downCount=" + downCount +
                ", weightedUp=" + weightedUp +
                ", weightedDown=" + weightedDown +
                ", relevanceCounts=" + relevanceCounts +
                ", difficultyCounts=" + difficultyCounts +
                ", totalFeedbacks=" + totalFeedbacks +
                '}';
    }
}
