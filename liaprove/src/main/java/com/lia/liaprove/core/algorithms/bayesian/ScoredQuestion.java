package com.lia.liaprove.core.algorithms.bayesian;

import com.lia.liaprove.core.domain.question.Question;
/**
 * Classe utilizada para auxiliar na sugestão de questões
 * personalizadas aos Recruiters.
 */
public class ScoredQuestion {
    private final Question question;
    private final double score;

    public ScoredQuestion(Question question, double score) {
        this.question = question;
        this.score = score;
    }

    public Question getQuestion() { return question; }
    public double getScore() { return score; }
}
