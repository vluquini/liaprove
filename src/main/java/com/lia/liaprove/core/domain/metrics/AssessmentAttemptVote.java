package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class AssessmentAttemptVote {
    private UUID id;
    private User user;
    private AssessmentAttempt assessmentAttempt;
    private VoteType voteType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AssessmentAttemptVote() {
    }

    public AssessmentAttemptVote(User user, AssessmentAttempt assessmentAttempt, VoteType voteType) {
        this.user = Objects.requireNonNull(user, "user cannot be null");
        this.assessmentAttempt = Objects.requireNonNull(assessmentAttempt, "assessmentAttempt cannot be null");
        this.voteType = Objects.requireNonNull(voteType, "voteType cannot be null");
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        if (this.id != null && !this.id.equals(id)) {
            throw new IllegalStateException("ID has already been set and cannot be changed.");
        }
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (this.user != null && !this.user.equals(user)) {
            throw new IllegalStateException("User has already been set and cannot be changed.");
        }
        this.user = user;
    }

    public AssessmentAttempt getAssessmentAttempt() {
        return assessmentAttempt;
    }

    public void setAssessmentAttempt(AssessmentAttempt assessmentAttempt) {
        if (this.assessmentAttempt != null && !this.assessmentAttempt.equals(assessmentAttempt)) {
            throw new IllegalStateException("Assessment attempt has already been set and cannot be changed.");
        }
        this.assessmentAttempt = assessmentAttempt;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteType voteType) {
        Objects.requireNonNull(voteType, "voteType cannot be null");
        if (this.voteType != voteType) {
            this.voteType = voteType;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        if (this.createdAt != null && !this.createdAt.equals(createdAt)) {
            throw new IllegalStateException("CreatedAt has already been set and cannot be changed.");
        }
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
