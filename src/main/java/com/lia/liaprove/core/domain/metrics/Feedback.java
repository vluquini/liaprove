package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Feedback {
    private UUID id;
    private User user;
    private String comment;
    private Vote vote;
    private LocalDateTime submissionDate;

    public Feedback() {}

    public Feedback(UUID id, User user, String comment, Vote vote, LocalDateTime submissionDate) {
        this.id = id;
        this.user = user;
        this.comment = comment;
        this.vote = vote;
        this.submissionDate = submissionDate;
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
}
