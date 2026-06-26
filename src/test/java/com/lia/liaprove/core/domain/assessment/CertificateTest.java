package com.lia.liaprove.core.domain.assessment;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CertificateTest {

    @Test
    void shouldStoreCertificateData() {
        UUID id = UUID.randomUUID();
        LocalDate issueDate = LocalDate.now();

        Certificate certificate = certificate(id, "CERT-123", "Certificate", "https://example.com/cert/CERT-123", issueDate, 95F);

        assertThat(certificate.getId()).isEqualTo(id);
        assertThat(certificate.getCertificateNumber()).isEqualTo("CERT-123");
        assertThat(certificate.getTitle()).isEqualTo("Certificate");
        assertThat(certificate.getDescription()).isEqualTo("Description");
        assertThat(certificate.getCertificateUrl()).isEqualTo("https://example.com/cert/CERT-123");
        assertThat(certificate.getIssueDate()).isEqualTo(issueDate);
        assertThat(certificate.getScore()).isEqualTo(95F);
    }

    @Test
    void shouldRejectBlankCertificateNumber() {
        assertThatThrownBy(() -> certificate(UUID.randomUUID(), " ", "Certificate", "https://example.com/cert", LocalDate.now(), 80F))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("certificateNumber must not be blank");
    }

    @Test
    void shouldRejectBlankTitle() {
        assertThatThrownBy(() -> certificate(UUID.randomUUID(), "CERT-123", " ", "https://example.com/cert", LocalDate.now(), 80F))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("title must not be blank");
    }

    @Test
    void shouldRejectBlankUrl() {
        assertThatThrownBy(() -> certificate(UUID.randomUUID(), "CERT-123", "Certificate", " ", LocalDate.now(), 80F))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("certificateUrl must not be blank");
    }

    @Test
    void shouldRejectNullIssueDate() {
        assertThatThrownBy(() -> certificate(UUID.randomUUID(), "CERT-123", "Certificate", "https://example.com/cert", null, 80F))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("issueDate must not be null");
    }

    @Test
    void shouldRejectInvalidScore() {
        assertThatThrownBy(() -> certificate(UUID.randomUUID(), "CERT-123", "Certificate", "https://example.com/cert", LocalDate.now(), 101F))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("score must be between 0 and 100");

        assertThatThrownBy(() -> certificate(UUID.randomUUID(), "CERT-123", "Certificate", "https://example.com/cert", LocalDate.now(), -1F))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("score must be between 0 and 100");

        assertThatThrownBy(() -> certificate(UUID.randomUUID(), "CERT-123", "Certificate", "https://example.com/cert", LocalDate.now(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("score must be between 0 and 100");
    }

    private Certificate certificate(
            UUID id,
            String certificateNumber,
            String title,
            String certificateUrl,
            LocalDate issueDate,
            Float score
    ) {
        return new Certificate(id, certificateNumber, title, "Description", certificateUrl, issueDate, score);
    }
}
