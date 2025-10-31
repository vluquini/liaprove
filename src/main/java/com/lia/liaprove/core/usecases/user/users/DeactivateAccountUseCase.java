package com.lia.liaprove.core.usecases.user.users;

import java.util.UUID;

/**
 * Caso de uso para desativar a conta de um usuário.
 * <p>
 * Pode lançar as seguintes exceções (unchecked):
 * - {@code UserNotFoundException} se o usuário não for encontrado.
 */
public interface DeactivateAccountUseCase {
    void deactivate(UUID userId);
}
