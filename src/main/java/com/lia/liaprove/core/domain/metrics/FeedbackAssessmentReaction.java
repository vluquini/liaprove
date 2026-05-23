package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class FeedbackAssessmentReaction {
    private UUID id;
    private User user;
    private ReactionType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FeedbackAssessmentReaction() {
        this.updatedAt = LocalDateTime.now();
    }

    public FeedbackAssessmentReaction(User user, ReactionType type) {
        this.user = Objects.requireNonNull(user, "user");
        this.type = Objects.requireNonNull(type, "type");
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
            throw new IllegalStateException("Reaction user has already been set and cannot be changed.");
        }
        this.user = Objects.requireNonNull(user, "User cannot be null when setting reaction user.");
    }

    public ReactionType getType() {
        return type;
    }

    public void setType(ReactionType newType) {
        if (newType == null) {
            return;
        }
        if (this.type != newType) {
            this.type = newType;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        if (this.createdAt != null && !this.createdAt.equals(createdAt)) {
            throw new IllegalStateException("Creation timestamp has already been set and cannot be changed.");
        }
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
