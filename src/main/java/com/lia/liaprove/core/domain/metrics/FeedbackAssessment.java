package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;

/**
 * Feedback deixado por um usuário sobre uma Assessment.
 *
 * - Associa-se a uma única Assessment (relação definida no construtor; não deve ser
 *   alterada depois da criação).
 * - Em regras de negócio, comentários de assessments podem ser privados (visíveis apenas
 *   ao recruiter e administradores) enquanto comentários de questões são públicos.
 */
public class FeedbackAssessment extends Feedback {
    private Assessment assessment;

    public FeedbackAssessment(User user, Assessment assessment, String comment,
                              LocalDateTime submissionDate, boolean visible) {
        super(user, comment, submissionDate, visible);
        this.assessment = assessment;
    }

    public Assessment getAssessment() {
        return assessment;
    }

}
