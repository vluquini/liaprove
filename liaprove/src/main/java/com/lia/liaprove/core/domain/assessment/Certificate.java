package com.lia.liaprove.core.domain.assessment;

import java.time.LocalDate;
import java.util.UUID;

public class Certificate {
    private UUID id;
    private String title;
    private String description;
    private String certificateUrl;
    private LocalDate issueDate;
    private Float score;

    public Certificate(UUID id, String title, String description, String certificateUrl, LocalDate issueDate, Float score) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.certificateUrl = certificateUrl;
        this.issueDate = issueDate;
        this.score = score;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCertificateUrl() {
        return certificateUrl;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }
}
