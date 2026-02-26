package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.CertificateGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.exceptions.assessment.CertificateEligibilityException;
import com.lia.liaprove.core.usecases.assessments.IssueCertificateUseCase;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Implementação do caso de uso para emissão de certificados.
 */
public class IssueCertificateUseCaseImpl implements IssueCertificateUseCase {

    private final CertificateGateway certificateGateway;
    private final String certificateBaseUrl;

    public IssueCertificateUseCaseImpl(CertificateGateway certificateGateway, String certificateBaseUrl) {
        this.certificateGateway = certificateGateway;
        this.certificateBaseUrl = certificateBaseUrl;
    }

    @Override
    public Certificate execute(AssessmentAttempt approvedAttempt) {
        // 1. Validar elegibilidade
        if (approvedAttempt.getStatus() != AssessmentAttemptStatus.APPROVED) {
            throw new CertificateEligibilityException("Only attempts with APPROVED status are eligible for certification.");
        }
        
        // 2. Gerar número e URL do certificado
        String certificateNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        
        String baseUrl = certificateBaseUrl.endsWith("/") ? certificateBaseUrl : certificateBaseUrl + "/";
        String certificateUrl = baseUrl + certificateNumber;

        // 3. Criar entidade Certificate
        // Passamos null no ID para que a infraestrutura (JPA) gere a chave primária
        Certificate certificate = new Certificate(
            null,
            certificateNumber,
            "Certificado de Conclusão: " + approvedAttempt.getAssessment().getTitle(),
            "Certificamos que " + approvedAttempt.getUser().getName() + " completou com sucesso a avaliação.",
            certificateUrl,
            LocalDate.now(),
            approvedAttempt.getAccuracyRate().floatValue()
        );

        // 4. Persistir
        return certificateGateway.save(certificate);
    }
}
