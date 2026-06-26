package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.UserRecruiter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
    private JobDescriptionAnalysis jobDescriptionAnalysis;

    public PersonalizedAssessment(UUID id, String title, String description, LocalDateTime creationDate,
                                  List<Question> questions, Duration evaluationTimer, UserRecruiter createdBy,
                                  LocalDateTime expirationDate, int maxAttempts, String shareableToken,
                                  PersonalizedAssessmentStatus status, AssessmentCriteriaWeights criteriaWeights) {
        this(id, title, description, creationDate, questions, evaluationTimer, createdBy, expirationDate,
                maxAttempts, shareableToken, status, criteriaWeights, null);
    }

    public PersonalizedAssessment(UUID id, String title, String description, LocalDateTime creationDate,
                                  List<Question> questions, Duration evaluationTimer, UserRecruiter createdBy,
                                  LocalDateTime expirationDate, int maxAttempts, String shareableToken,
                                  PersonalizedAssessmentStatus status, AssessmentCriteriaWeights criteriaWeights,
                                  JobDescriptionAnalysis jobDescriptionAnalysis) {
        super(id, title, description, creationDate, questions, evaluationTimer);
        this.createdBy = createdBy;
        this.expirationDate = expirationDate;
        this.totalAttempts = 0;
        setMaxAttempts(maxAttempts);
        this.shareableToken = shareableToken;
        this.status = Objects.requireNonNull(status, "status must not be null");
        setCriteriaWeights(criteriaWeights);
        this.jobDescriptionAnalysis = jobDescriptionAnalysis;
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
        if (totalAttempts < 0) {
            throw new IllegalArgumentException("totalAttempts must not be negative");
        }
        this.totalAttempts = totalAttempts;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("maxAttempts must be greater than zero");
        }
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

    public void activate() {
        this.status = PersonalizedAssessmentStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = PersonalizedAssessmentStatus.DEACTIVATED;
    }

    public void revoke() {
        this.status = PersonalizedAssessmentStatus.REVOKED;
    }

    public void close() {
        this.status = PersonalizedAssessmentStatus.CLOSED;
    }

    public AssessmentCriteriaWeights getCriteriaWeights() {
        return criteriaWeights;
    }

    public void setCriteriaWeights(AssessmentCriteriaWeights criteriaWeights) {
        this.criteriaWeights = criteriaWeights == null ? AssessmentCriteriaWeights.defaultWeights() : criteriaWeights;
    }

    public JobDescriptionAnalysis getJobDescriptionAnalysis() {
        return jobDescriptionAnalysis;
    }

    public void setJobDescriptionAnalysis(JobDescriptionAnalysis jobDescriptionAnalysis) {
        this.jobDescriptionAnalysis = jobDescriptionAnalysis;
    }

    public boolean isExpired(LocalDateTime referenceDate) {
        return expirationDate != null && referenceDate != null && referenceDate.isAfter(expirationDate);
    }

    public boolean hasReachedMaxAttempts(long currentAttempts) {
        return currentAttempts >= maxAttempts;
    }
}
