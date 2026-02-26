package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.exceptions.assessment.CertificateNotFoundException;

/**
 * Caso de uso para buscar e validar publicamente um certificado pelo seu número identificador.
 * <p>
 * Permite que qualquer pessoa ou sistema verifique a autenticidade de um certificado
 * a partir de sua URL pública (ex: liaprove.com/certificates/{certificateNumber}).
 */
public interface GetCertificateByNumberUseCase {

    /**
     * Busca e retorna um certificado com base no número de identificação fornecido.
     *
     * @param certificateNumber O número único do certificado a ser validado.
     * @return O objeto Certificate correspondente.
     * @throws CertificateNotFoundException se nenhum certificado for encontrado com o número fornecido.
     */
    Certificate execute(String certificateNumber);
}
