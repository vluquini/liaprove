package com.lia.liaprove.core.usecases.user.admin;

import java.util.UUID;

/**
 * Caso de uso para ativar/desativar a conta de um usuário.
 * <p>
 * Pode lançar as seguintes exceções (unchecked):
 * - {@code UserNotFoundException} se o usuário não for encontrado.
 */
public interface UserModerationUseCase {
    void activateUser(UUID userId, UUID adminId);
    void deactivateUser(UUID userId, UUID adminId);
}

