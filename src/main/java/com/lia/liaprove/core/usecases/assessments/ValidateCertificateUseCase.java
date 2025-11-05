package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.exceptions.CertificateNotFoundException;

/**
 * Serviço de gerenciamento de certificados no domínio.
 *
 * <p>Responsabilidade: atribuir e revogar certificados de usuários. Este contrato é
 * genérico — pode ser invocado por um usuário administrador ou pelo sistema.
 */
public interface ValidateCertificateUseCase {

    /**
     * Busca e retorna um certificado com base no número de identificação fornecido.
     *
     * @param certificateNumber O número único do certificado a ser validado.
     * @return O objeto Certificate correspondente.
     * @throws CertificateNotFoundException se nenhum certificado for encontrado com o número fornecido.
     */
    Certificate execute(String certificateNumber);
}
