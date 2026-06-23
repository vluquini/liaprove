package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.User;

import java.util.Objects;

/**
 * Representa o voto formal de um usuário em uma questão, indicando aprovação ou rejeição.
 * Esta entidade é fundamental para o processo de curadoria de questões e alimenta
 * a rede Bayesiana para determinar a aprovação de uma questão.
 * Um voto é distinto de um feedback textual.
 */
public class QuestionVote extends Vote {
    private Question question;

    public QuestionVote() {
        // Required for frameworks like JPA and MapStruct
    }

    public QuestionVote(User user, Question question, VoteType voteType) {
        super(user, voteType);
        this.question = Objects.requireNonNull(question, "question cannot be null");
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        if (this.question != null && !this.question.equals(question)) {
            throw new IllegalStateException("Question has already been set and cannot be changed.");
        }
        this.question = question;
    }
}
