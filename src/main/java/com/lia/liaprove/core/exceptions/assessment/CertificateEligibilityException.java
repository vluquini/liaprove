package com.lia.liaprove.core.exceptions.assessment;

/**
 * Exceção lançada quando se tenta emitir um certificado para uma tentativa
 * de avaliação que não atende aos critérios de aprovação (status não APPROVED).
 */
public class CertificateEligibilityException extends RuntimeException {
    public CertificateEligibilityException(String message) {
        super(message);
    }
}
