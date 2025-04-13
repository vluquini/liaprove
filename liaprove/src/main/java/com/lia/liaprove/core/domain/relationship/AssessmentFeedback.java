package com.lia.liaprove.core.domain.relationship;

import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.metrics.Feedback;

import java.time.LocalDateTime;

public class AssessmentFeedback extends Feedback {
    private Assessment assessment;

    public AssessmentFeedback(Assessment assessment,String comment, LocalDateTime submissionDate) {
        super(comment, submissionDate);
        this.assessment = assessment;
    }
}
