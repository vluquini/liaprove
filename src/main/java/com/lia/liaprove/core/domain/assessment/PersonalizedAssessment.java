package com.lia.liaprove.core.domain.assessment;

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
    private LocalDateTime expirationDate;
    private int totalAttempts;
    private int maxAttempts;
    private String shareableToken;
    private PersonalizedAssessmentStatus status;
    private AssessmentCriteriaWeights criteriaWeights;

    public PersonalizedAssessment(UUID id, String title, String description, LocalDateTime creationDate,
                                  List<Question> questions, Duration evaluationTimer, UserRecruiter createdBy,
                                  LocalDateTime expirationDate, int totalAttempts, int maxAttempts, String shareableToken,
                                  PersonalizedAssessmentStatus status, AssessmentCriteriaWeights criteriaWeights) {
        super(id, title, description, creationDate, questions, evaluationTimer);
        this.createdBy = createdBy;
        this.expirationDate = expirationDate;
        this.totalAttempts = totalAttempts;
        this.maxAttempts = maxAttempts;
        this.shareableToken = shareableToken;
        this.status = status;
        this.criteriaWeights = criteriaWeights == null ? AssessmentCriteriaWeights.defaultWeights() : criteriaWeights;
    }

    public PersonalizedAssessment(UUID id, String title, String description, LocalDateTime creationDate,
                                  List<Question> questions, Duration evaluationTimer, UserRecruiter createdBy,
                                  LocalDateTime expirationDate, int totalAttempts, int maxAttempts, String shareableToken,
                                  PersonalizedAssessmentStatus status) {
        this(id, title, description, creationDate, questions, evaluationTimer, createdBy, expirationDate,
                totalAttempts, maxAttempts, shareableToken, status, AssessmentCriteriaWeights.defaultWeights());
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

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public String getShareableToken() {
        return shareableToken;
    }

    public void setShareableToken(String shareableToken) {
        this.shareableToken = shareableToken;
    }

    public PersonalizedAssessmentStatus getStatus() {
        return status;
    }

    public void setStatus(PersonalizedAssessmentStatus status) {
        this.status = status;
    }

    public AssessmentCriteriaWeights getCriteriaWeights() {
        return criteriaWeights;
    }

    public void setCriteriaWeights(AssessmentCriteriaWeights criteriaWeights) {
        this.criteriaWeights = criteriaWeights == null ? AssessmentCriteriaWeights.defaultWeights() : criteriaWeights;
    }
}
