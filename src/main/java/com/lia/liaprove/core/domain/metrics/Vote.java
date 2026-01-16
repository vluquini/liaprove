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

    public Vote() {
        // Required for frameworks like JPA and MapStruct
    }

    public Vote(User user, Question question, VoteType voteType) {
        this.user = Objects.requireNonNull(user, "user cannot be null");
        this.question = Objects.requireNonNull(question, "question cannot be null");
        this.voteType = Objects.requireNonNull(voteType, "voteType cannot be null");
        this.createdAt = LocalDateTime.now();
    }
    
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        if (this.id != null) {
            throw new IllegalStateException("ID has already been set and cannot be changed.");
        }
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (this.user != null) {
            throw new IllegalStateException("User has already been set and cannot be changed.");
        }
        this.user = user;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        if (this.question != null) {
            throw new IllegalStateException("Question has already been set and cannot be changed.");
        }
        this.question = question;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteType voteType) {
        if (this.voteType != null) {
            throw new IllegalStateException("VoteType has already been set and cannot be changed.");
        }
        this.voteType = voteType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        if (this.createdAt != null) {
            throw new IllegalStateException("CreatedAt has already been set and cannot be changed.");
        }
        this.createdAt = createdAt;
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