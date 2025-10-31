package com.lia.liaprove.core.usecases.user.metrics;

import com.lia.liaprove.core.domain.assessment.Certificate;

import java.util.UUID;

/**
 * Serviço de gerenciamento de certificados no domínio.
 *
 * <p>Responsabilidade: atribuir e revogar certificados de usuários. Este contrato é
 * genérico — pode ser invocado por um usuário administrador ou pelo sistema.
 *
 * <p>Possíveis Exceptions: UserNotFoundException, CertificateNotFoundException, AuthorizationException.
 */
public interface CertificateServiceUseCase {

    /**
     * Atribui/emite um Certificate para o usuário alvo.
     *
     * @param actorId      id do ator que está solicitando a atribuição (admin ou sistema)
     * @param targetUserId id do usuário que receberá o certificado
     * @param certificate  dados do certificado a ser atribuído
     */
    void assignCertificate(UUID actorId, UUID targetUserId, Certificate certificate);

    /**
     * Revoga um Certificado identificado do usuário alvo.
     *
     * @param actorId           id do ator que está solicitando a revogação (admin ou sistema)
     * @param targetUserId      id do usuário cujo certificado será revogado
     * @param certificateNumber identificador (número) do certificado a revogar
     */
    void revokeCertificate(UUID actorId, UUID targetUserId, String certificateNumber);
}
