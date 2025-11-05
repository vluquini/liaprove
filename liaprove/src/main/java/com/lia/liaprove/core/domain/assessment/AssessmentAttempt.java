package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa uma tentativa específica de um usuário ao realizar uma avaliação (Assessment).
 * Armazena informações sobre o progresso, resultados e o status da tentativa.
 */
public class AssessmentAttempt {
    private UUID id;
    private Assessment assessment;
    private User user;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Integer accuracyRate;
    private Certificate certificate;
    private AssessmentAttemptStatus status;

    public AssessmentAttempt(UUID id, Assessment assessment, User user, LocalDateTime startedAt, LocalDateTime finishedAt,
                             Integer accuracyRate, Certificate certificate, AssessmentAttemptStatus status) {
        this.id = id;
        this.assessment = assessment;
        this.user = user;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.accuracyRate = accuracyRate;
        this.certificate = certificate;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Integer getAccuracyRate() {
        return accuracyRate;
    }

    public void setAccuracyRate(Integer accuracyRate) {
        this.accuracyRate = accuracyRate;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public AssessmentAttemptStatus getStatus() {
        return status;
    }

    public void setStatus(AssessmentAttemptStatus status) {
        this.status = status;
    }
}
