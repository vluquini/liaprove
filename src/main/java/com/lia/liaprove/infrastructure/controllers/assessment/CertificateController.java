package com.lia.liaprove.infrastructure.controllers.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.exceptions.assessment.CertificateNotFoundException;
import com.lia.liaprove.core.usecases.assessments.GetCertificateByNumberUseCase;
import com.lia.liaprove.infrastructure.dtos.assessment.CertificateVerificationResponse;
import com.lia.liaprove.infrastructure.mappers.assessment.CertificateDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/certificates")
public class CertificateController {

    private final GetCertificateByNumberUseCase getCertificateByNumberUseCase;
    private final AssessmentAttemptGateway assessmentAttemptGateway;
    private final CertificateDtoMapper certificateDtoMapper;

    public CertificateController(GetCertificateByNumberUseCase getCertificateByNumberUseCase,
                                 AssessmentAttemptGateway assessmentAttemptGateway,
                                 CertificateDtoMapper certificateDtoMapper) {
        this.getCertificateByNumberUseCase = getCertificateByNumberUseCase;
        this.assessmentAttemptGateway = assessmentAttemptGateway;
        this.certificateDtoMapper = certificateDtoMapper;
    }

    @GetMapping("/{certificateNumber}")
    public ResponseEntity<CertificateVerificationResponse> verifyCertificate(
            @PathVariable String certificateNumber) {

        Certificate certificate = getCertificateByNumberUseCase.execute(certificateNumber);

        AssessmentAttempt attempt = assessmentAttemptGateway.findByCertificateNumber(certificateNumber)
                .orElseThrow(() -> new CertificateNotFoundException(
                        "Certificate owner not found for number " + certificateNumber + "."
                ));

        CertificateVerificationResponse response = certificateDtoMapper
                .toVerificationResponse(certificate, attempt.getUser());

        return ResponseEntity.ok(response);
    }
}
