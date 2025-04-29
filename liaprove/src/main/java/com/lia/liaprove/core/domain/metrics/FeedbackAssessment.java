package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.assessment.Assessment;

import java.time.LocalDateTime;
import java.util.UUID;

public class FeedbackAssessment extends Feedback {
    private UUID id;
    private Assessment assessment;

    public FeedbackAssessment(Assessment assessment, String comment, LocalDateTime submissionDate) {
        super(comment, submissionDate);
        this.assessment = assessment;
    }
}
