package com.lia.liaprove.core.usecases.user.admin;

import java.util.UUID;

/**
 * Caso de uso para ativar/desativar a conta de um usuário.
 * Permite que admins moderem usuários e que usuários desativem suas próprias contas.
 */
public interface UserModerationUseCase {
    void activateUser(UUID targetUserId, UUID actorId);
    void deactivateUser(UUID targetUserId, UUID actorId);
}
