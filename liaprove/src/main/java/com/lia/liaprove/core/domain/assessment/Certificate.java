package com.lia.liaprove.core.domain.assessment;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Representa um certificado emitido a um usuário após a conclusão bem-sucedida de uma avaliação.
 * Contém informações como título, descrição, data de emissão e pontuação obtida.
 */
public class Certificate {
    private UUID id;
    // Número identificador do Certificado (será concatenado à URL)
    private String certificateNumber;
    private String title;
    private String description;
    private String certificateUrl;
    private LocalDate issueDate;
    private Float score;

    public Certificate(UUID id, String certificateNumber, String title, String description,
                       String certificateUrl, LocalDate issueDate, Float score) {
        this.id = id;
        this.certificateNumber = certificateNumber;
        this.title = title;
        this.description = description;
        this.certificateUrl = certificateUrl;
        this.issueDate = issueDate;
        this.score = score;
    }

    public UUID getId() {
        return id;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCertificateUrl() {
        return certificateUrl;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public Float getScore() {
        return score;
    }
}
