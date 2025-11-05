package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.metrics.FeedbackAssessment;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.UserRecruiter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Representa uma avaliação personalizada criada por um recrutador.
 * Estende a classe base Assessment e adiciona propriedades específicas
 * como o criador, data de expiração e status da avaliação personalizada.
 */
public class PersonalizedAssessment extends Assessment {
    private UserRecruiter createdBy;
    // Data de expiração da avaliação
    private LocalDateTime expirationDate;
    // Total de vezes que a avaliação foi realizada
    private int totalAttempts;
    private boolean allowsRetake;
    // Status da avaliação
    private PersonalizedAssessmentStatus status;

    public PersonalizedAssessment(UUID id, String title, String description, LocalDateTime creationDate, List<Question> questions, List<FeedbackAssessment> feedbacks, Duration evaluationTimer, UserRecruiter createdBy, LocalDateTime expirationDate, int totalAttempts, boolean allowsRetake, PersonalizedAssessmentStatus status) {
        super(id, title, description, creationDate, questions, feedbacks, evaluationTimer);
        this.createdBy = createdBy;
        this.expirationDate = expirationDate;
        this.totalAttempts = totalAttempts;
        this.allowsRetake = allowsRetake;
        this.status = status;
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

    public PersonalizedAssessmentStatus getStatus() {
        return status;
    }

    public void setStatus(PersonalizedAssessmentStatus status) {
        this.status = status;
    }
}
