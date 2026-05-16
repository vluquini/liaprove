package com.lia.liaprove.application.services.user;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.core.domain.assessment.Certificate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListMyCertificatesUseCaseImplTest {

    @Mock
    private AssessmentAttemptGateway assessmentAttemptGateway;

    @InjectMocks
    private ListMyCertificatesUseCaseImpl listMyCertificatesUseCase;

    @Test
    void shouldListCertificatesForAuthenticatedUser() {
        UUID userId = UUID.randomUUID();
        Certificate certificate = new Certificate(
                UUID.randomUUID(),
                "CERT-123",
                "Certificado Java",
                "Certificado emitido pelo LIA Prove.",
                "https://liaprove.com/certificates/CERT-123",
                LocalDate.of(2026, 5, 16),
                90F
        );
        when(assessmentAttemptGateway.findCertificatesByUserId(userId)).thenReturn(List.of(certificate));

        List<Certificate> certificates = listMyCertificatesUseCase.execute(userId);

        assertThat(certificates).containsExactly(certificate);
        verify(assessmentAttemptGateway).findCertificatesByUserId(userId);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoCertificates() {
        UUID userId = UUID.randomUUID();
        when(assessmentAttemptGateway.findCertificatesByUserId(userId)).thenReturn(List.of());

        List<Certificate> certificates = listMyCertificatesUseCase.execute(userId);

        assertThat(certificates).isEmpty();
    }
}
