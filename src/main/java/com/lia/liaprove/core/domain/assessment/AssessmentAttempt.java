package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

/*
Esta entidade representa uma avaliação feita por um Usuário.
Isto pois: um SystemAssessment pode ser feita por apenas um User
e uma PersonalizedAssessment pode ser feita por N Users.
 */
public class AssessmentAttempt {
    private UUID id;
    private Assessment assessment;
    private User user;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Integer accuracyRate;
    private Certificate certificate;

    public AssessmentAttempt(UUID id, Assessment assessment, User user, LocalDateTime startedAt, LocalDateTime finishedAt, Integer accuracyRate, Certificate certificate) {
        this.id = UUID.randomUUID();
        this.assessment = assessment;
        this.user = user;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.accuracyRate = accuracyRate;
        this.certificate = certificate;
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
}
