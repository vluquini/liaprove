package com.lia.liaprove.core.domain.metrics;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Feedback {
    private UUID id;
    private String comment;
    private LocalDateTime submissionDate;

    public Feedback(String comment, LocalDateTime submissionDate) {
        this.comment = comment;
        this.submissionDate = submissionDate;
    }
}
