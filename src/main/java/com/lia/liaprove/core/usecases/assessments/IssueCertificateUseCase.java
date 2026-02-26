package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.exceptions.CertificateEligibilityException;

/**
 * Caso de uso para emitir um certificado para um usuário aprovado em uma avaliação.
 */
public interface IssueCertificateUseCase {

    /**
     * Emite um certificado para a tentativa de avaliação fornecida.
     *
     * @param approvedAttempt A tentativa de avaliação com status APPROVED.
     * @return O objeto Certificate gerado e persistido.
     * @throws CertificateEligibilityException se a tentativa não tiver status APPROVED.
     */
    Certificate execute(AssessmentAttempt approvedAttempt);
}
