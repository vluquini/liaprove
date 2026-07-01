package com.lia.liaprove.core.algorithms.bayesian;

import com.lia.liaprove.core.domain.question.Question;

import java.util.Objects;

/**
 * Classe utilizada para auxiliar na sugestão de questões
 * personalizadas aos Recruiters.
 */
public class ScoredQuestion {
    private final Question question;
    private final double score;

    public ScoredQuestion(Question question, double score) {
        if (!Double.isFinite(score) || score < 0.0 || score > 1.0) {
            throw new IllegalArgumentException("score must be finite and between 0.0 and 1.0");
        }
        this.question = Objects.requireNonNull(question, "question must not be null");
        this.score = score;
    }

    public Question getQuestion() {
        return question;
    }

    public double getScore() {
        return score;
    }
}
