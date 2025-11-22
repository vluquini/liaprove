package com.lia.liaprove.application.services.user;

import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.exceptions.UserNotFoundException;
import com.lia.liaprove.core.usecases.user.users.DeleteUserUseCase;

import java.util.Objects;
import java.util.UUID;

/**
 * Implementação do caso de uso "DeleteUserUseCase".
 *
 * Observações:
 * - Depende apenas da porta (gateway): UserGateway.
 * - Garante que o usuário existe antes de tentar deletá-lo.
 */
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {

    private final UserGateway userGateway;

    public DeleteUserUseCaseImpl(UserGateway userGateway) {
        this.userGateway = Objects.requireNonNull(userGateway, "userGateway must not be null");
    }

    @Override
    public void delete(UUID userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        userGateway.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        userGateway.deleteById(userId);
    }
}
