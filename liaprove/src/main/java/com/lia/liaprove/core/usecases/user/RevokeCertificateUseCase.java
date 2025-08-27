package com.lia.liaprove.core.usecases.user;

import com.lia.liaprove.core.exceptions.AuthorizationException;
import com.lia.liaprove.core.exceptions.CertificateNotFoundException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;

import java.util.UUID;

/**
 * Revoga um certificado — caso de uso administrativo.
 */
public interface RevokeCertificateUseCase {
    void revokeCertificate(UUID actorId, UUID targetUserId,
                           String certificateNumber) throws UserNotFoundException, CertificateNotFoundException, AuthorizationException;
}