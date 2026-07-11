package com.lia.liaprove.core.algorithms.bayesian;

import com.lia.liaprove.core.domain.question.QuestionStatus;

import java.util.Objects;
import java.util.UUID;

/**
 * Resultado calculado pelo motor bayesiano para uma questão em votação.
 */
public class BayesianVotingDecision {
    private final UUID questionId;
    private final double approvalProbability;
    private final QuestionStatus resultStatus;
    private final double evidenceWeight;
    private final double approvalThreshold;

    public BayesianVotingDecision(UUID questionId, double approvalProbability, QuestionStatus resultStatus,
                                  double evidenceWeight, double approvalThreshold) {
        validateProbability(approvalProbability);
        validateResultStatus(resultStatus);
        validateEvidenceWeight(evidenceWeight);
        validateApprovalThreshold(approvalThreshold);

        this.questionId = Objects.requireNonNull(questionId, "questionId must not be null");
        this.approvalProbability = approvalProbability;
        this.resultStatus = resultStatus;
        this.evidenceWeight = evidenceWeight;
        this.approvalThreshold = approvalThreshold;
    }

    private static void validateProbability(double approvalProbability) {
        if (!Double.isFinite(approvalProbability) || approvalProbability < 0.0 || approvalProbability > 1.0) {
            throw new IllegalArgumentException("approvalProbability must be finite and between 0.0 and 1.0");
        }
    }

    private static void validateResultStatus(QuestionStatus resultStatus) {
        if (resultStatus != QuestionStatus.APPROVED && resultStatus != QuestionStatus.REJECTED) {
            throw new IllegalArgumentException("resultStatus must be APPROVED or REJECTED");
        }
    }

    private static void validateEvidenceWeight(double evidenceWeight) {
        if (!Double.isFinite(evidenceWeight) || evidenceWeight < 0.0) {
            throw new IllegalArgumentException("evidenceWeight must be finite and >= 0");
        }
    }

    private static void validateApprovalThreshold(double approvalThreshold) {
        if (!Double.isFinite(approvalThreshold) || approvalThreshold < 0.0 || approvalThreshold > 1.0) {
            throw new IllegalArgumentException("approvalThreshold must be finite and between 0.0 and 1.0");
        }
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public double getApprovalProbability() {
        return approvalProbability;
    }

    public QuestionStatus getResultStatus() {
        return resultStatus;
    }

    public double getEvidenceWeight() {
        return evidenceWeight;
    }

    public double getApprovalThreshold() {
        return approvalThreshold;
    }

    public boolean isApproved() {
        return resultStatus == QuestionStatus.APPROVED;
    }
}
