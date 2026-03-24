package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.CertificateGateway;
import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.exceptions.assessment.CertificateNotFoundException;
import com.lia.liaprove.core.usecases.assessments.GetCertificateByNumberUseCase;

/**
 * Implementação do caso de uso para buscar e validar publicamente um certificado pelo seu número.
 */
public class GetCertificateByNumberUseCaseImpl implements GetCertificateByNumberUseCase {

    private final CertificateGateway certificateGateway;

    public GetCertificateByNumberUseCaseImpl(CertificateGateway certificateGateway) {
        this.certificateGateway = certificateGateway;
    }

    @Override
    public Certificate execute(String certificateNumber) {
        if (certificateNumber == null || certificateNumber.isBlank()) {
            throw new CertificateNotFoundException("Certificate number must not be null or blank.");
        }

        return certificateGateway.findByCertificateNumber(certificateNumber)
                .orElseThrow(() -> new CertificateNotFoundException(
                        "Certificate with number " + certificateNumber + " not found."
                ));
    }
}
