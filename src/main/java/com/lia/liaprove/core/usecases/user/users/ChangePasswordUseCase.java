package com.lia.liaprove.core.usecases.user.users;

import java.util.UUID;

/**
 * Caso de uso para alterar a senha de um usuário.
 * <p>
 * Pode lançar as seguintes exceções (unchecked):
 * - {@code UserNotFoundException} se o usuário não for encontrado.
 * - {@code InvalidCredentialsException} se a senha antiga fornecida for inválida.
 */
public interface ChangePasswordUseCase {
    void changePassword(UUID userId, String oldPassword, String newPassword);
}
