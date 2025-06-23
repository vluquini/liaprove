package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.metrics.Feedback;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.UserRecruiter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/*
Esta entidade representa Assessments personalizados
criados por um Recruiter.
 */
public class PersonalizedAssessment extends Assessment {
    private UserRecruiter createdBy;
    // Data de expiração da avaliação
    private LocalDateTime expirationDate;
    // Total de vezes que a avaliação foi realizada
    private int totalAttempts;
    // Status da avaliação
    private AssessmentStatus status;

    public PersonalizedAssessment(UUID id, String title, String description, LocalDateTime creationDate, List<Question> questions, List<Feedback> feedbacks, Duration evaluationTimer) {
        super(id, title, description, creationDate, questions, feedbacks, evaluationTimer);
    }

    public UserRecruiter getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserRecruiter createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(int totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public AssessmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssessmentStatus status) {
        this.status = status;
    }
}
