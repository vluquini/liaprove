package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa o voto formal de um usuário em uma questão, indicando aprovação ou rejeição.
 * Esta entidade é fundamental para o processo de curadoria de questões e alimenta
 * a rede Bayesiana para determinar a aprovação de uma questão.
 * Um voto é distinto de um feedback textual.
 */
public class Vote {
    private UUID id;
    private User user;
    private Question question;
    private VoteType voteType;
    private LocalDateTime createdAt;

    public Vote(User user, Question question, VoteType voteType) {
        this.id = UUID.randomUUID();
        this.user = Objects.requireNonNull(user, "user cannot be null");
        this.question = Objects.requireNonNull(question, "question cannot be null");
        this.voteType = Objects.requireNonNull(voteType, "voteType cannot be null");
        this.createdAt = LocalDateTime.now();
    }
    
    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Question getQuestion() {
        return question;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteType voteType) {
        this.voteType = voteType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return Objects.equals(id, vote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}