package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.metrics.FeedbackAssessment;
import com.lia.liaprove.core.domain.question.Question;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/*
Esta classe refere-se a Assessments criados pelo sistema.
 */
public class SystemAssessment extends Assessment{
    public SystemAssessment(UUID id, String title, String description, LocalDateTime creationDate, List<Question> questions, List<FeedbackAssessment> feedbacks, Duration evaluationTimer) {
        super(id, title, description, creationDate, questions, feedbacks, evaluationTimer);
    }

}
