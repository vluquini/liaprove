package com.lia.liaprove.core.domain.metrics;

import java.time.LocalDateTime;

public abstract class Feedback {
    private String comment;
    private Integer upVote;
    private Integer downVote;
    private LocalDateTime submissionDate;

    public Feedback(String comment, LocalDateTime submissionDate) {
        this.comment = comment;
        this.upVote = 0;
        this.downVote = 0;
        this.submissionDate = submissionDate;
    }
}
