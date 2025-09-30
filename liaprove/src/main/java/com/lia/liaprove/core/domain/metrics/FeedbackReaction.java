package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade que representa a reação (like/dislike) de um usuário a um FeedbackQuestion.
 * Mantemos referência ao usuário para evitar múltiplas reações do mesmo usuário.
 */
public final class FeedbackReaction {
    private final UUID id;
    private final User user;
    private ReactionType type;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FeedbackReaction(User user, ReactionType type) {
        this.id = UUID.randomUUID();
        this.user = Objects.requireNonNull(user, "user");
        this.type = Objects.requireNonNull(type, "type");
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public ReactionType getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setType(ReactionType newType) {
        if (newType == null) return;
        if (this.type != newType) {
            this.type = newType;
            this.updatedAt = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeedbackReaction)) return false;
        FeedbackReaction that = (FeedbackReaction) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
