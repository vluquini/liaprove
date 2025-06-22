package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.assessment.Assessment;

import java.time.LocalDateTime;
import java.util.UUID;
/*
Classe que será utilizada para usuários avaliarem os Assessments
personalizados.
 */
public class FeedbackAssessment extends Feedback {
    private UUID id;
    private Assessment assessment;

    public FeedbackAssessment(Assessment assessment, String comment, LocalDateTime submissionDate) {
        super(comment, submissionDate);
        this.assessment = assessment;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }
}
