package com.lia.liaprove.core.usecases.user.metrics;

import com.lia.liaprove.core.exceptions.AuthorizationException;
import com.lia.liaprove.core.exceptions.InvalidUserDataException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;

import java.util.UUID;

/**
 * Emite um certificado para um usuário (orquestra regras de permissão, validações, persistência).
 */
public interface IssueCertificateUseCase {
    void issueCertificate(UUID actorId, UUID targetUserId, String certificateNumber, String title,
                          Float score) throws UserNotFoundException, AuthorizationException, InvalidUserDataException;
}