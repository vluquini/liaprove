package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public abstract class Feedback {
    private UUID id;
    private User user;
    private String comment;
    private Vote vote;
    private LocalDateTime submissionDate;
    private LocalDateTime updatedAt;
    private boolean visible;

    public Feedback() {}

    public Feedback(UUID id, User user, String comment, Vote vote, LocalDateTime submissionDate, boolean visible) {
        this.id = id;
        this.user = user;
        this.comment = comment;
        this.vote = vote;
        this.submissionDate = submissionDate;
        this.visible = true;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Vote getVote() {
        return vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            touchUpdatedAt();
        }
    }

    // Domain methods

    /**
     * Edita o comentário do feedback e atualiza o timestamp.
     * @param newComment texto novo (nulo é convertido para empty string)
     */
    public void editComment(String newComment) {
        String normalized = newComment == null ? "" : newComment.trim();
        if (!Objects.equals(this.comment, normalized)) {
            this.comment = normalized;
            touchUpdatedAt();
        }
    }

    /**
     * Marca o feedback como não visível (soft-delete / hide).
     * Atualiza updatedAt.
     */
    public void hide() {
        if (this.visible) {
            this.visible = false;
            touchUpdatedAt();
        }
    }

    /**
     * Torna o feedback visível novamente.
     * Atualiza updatedAt.
     */
    public void show() {
        if (!this.visible) {
            this.visible = true;
            touchUpdatedAt();
        }
    }

    /**
     * Touch: atualiza o campo updatedAt para o tempo atual.
     * Uso interno por métodos de domínio; pode ser útil expor em casos especiais.
     */
    protected void touchUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }

}
