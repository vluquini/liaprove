package com.lia.liaprove.core.domain.metrics;

import java.time.LocalDateTime;

public abstract class Feedback {
    private String comment;
    private LocalDateTime submissionDate;

    public Feedback(String comment, LocalDateTime submissionDate) {
        this.comment = comment;
        this.submissionDate = submissionDate;
    }
}
