package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

/*
Classe que será utilizada para usuários avaliarem os Assessments
personalizados.
 */
public class FeedbackAssessment extends Feedback {
    private Assessment assessment;

    public FeedbackAssessment(UUID id, User user, Assessment assessment, String comment, Vote vote, LocalDateTime submissionDate) {
        super(id, user, comment, vote, submissionDate);
        this.assessment = assessment;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }
}
