package com.lia.liaprove.core.domain.relationship;

import com.lia.liaprove.core.domain.metrics.Feedback;
import com.lia.liaprove.core.domain.question.Question;

import java.time.LocalDateTime;

public class QuestionFeedback extends Feedback{
    private Question question;

    public QuestionFeedback(Question question, String comment, LocalDateTime submissionDate) {
        super(comment, submissionDate);
        this.question = question;
    }
}
