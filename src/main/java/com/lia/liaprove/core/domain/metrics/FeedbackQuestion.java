package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.question.Question;

import java.time.LocalDateTime;
import java.util.UUID;

public class FeedbackQuestion extends Feedback{
    private UUID id;
    private Integer upVote;
    private Integer downVote;
    private Question question;

    public FeedbackQuestion(Question question, String comment, LocalDateTime submissionDate) {
        super(comment, submissionDate);
        this.upVote = 0;
        this.downVote = 0;
        this.question = question;
    }
}
