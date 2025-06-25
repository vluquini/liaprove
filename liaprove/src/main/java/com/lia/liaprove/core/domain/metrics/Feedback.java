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

    public Feedback(UUID id, User user, String comment, Vote vote, LocalDateTime submissionDate) {
        this.id = id;
        this.user = user;
        this.comment = comment;
        this.vote = vote;
        this.submissionDate = submissionDate;
    }
}
