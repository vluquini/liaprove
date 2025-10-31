package com.lia.liaprove.core.usecases.user.users;

import com.lia.liaprove.core.domain.user.User;

import java.util.UUID;

/**
 * Caso de uso para buscar um usuário específico pelo seu UUID.
 * <p>
 * Pode lançar as seguintes exceções (unchecked):
 * - {@code UserNotFoundException} se nenhum usuário corresponder ao ID fornecido.
 */
public interface GetUserByIdUseCase {
    User findById(UUID userId);
}
