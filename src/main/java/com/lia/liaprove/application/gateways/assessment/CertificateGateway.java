package com.lia.liaprove.application.gateways.assessment;

import com.lia.liaprove.core.domain.assessment.Certificate;
import java.util.Optional;

/**
 * Gateway para operações de persistência e recuperação de Certificados.
 */
public interface CertificateGateway {

    /**
     * Salva um certificado emitido.
     * @param certificate O certificado a ser salvo.
     * @return O certificado salvo (com ID gerado, se aplicável).
     */
    Certificate save(Certificate certificate);

    /**
     * Busca um certificado pelo seu número identificador.
     * @param certificateNumber O número identificador do certificado.
     * @return Um Optional contendo o certificado, se encontrado.
     */
    Optional<Certificate> findByCertificateNumber(String certificateNumber);
}
