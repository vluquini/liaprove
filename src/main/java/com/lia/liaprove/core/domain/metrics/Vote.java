package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public abstract class Vote {
    private UUID id;
    private User user;
    private VoteType voteType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected Vote() {
    }

    protected Vote(User user, VoteType voteType) {
        this.user = Objects.requireNonNull(user, "user cannot be null");
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

    public VoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteType voteType) {
        Objects.requireNonNull(voteType, "voteType cannot be null");
        if (this.voteType != voteType) {
            this.voteType = voteType;
            touchUpdatedAt();
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

    protected void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
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
