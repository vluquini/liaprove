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
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AssessmentAttempt getAssessmentAttempt() {
        return assessmentAttempt;
    }

    public void setAssessmentAttempt(AssessmentAttempt assessmentAttempt) {
        this.assessmentAttempt = assessmentAttempt;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteType voteType) {
        this.voteType = voteType;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
