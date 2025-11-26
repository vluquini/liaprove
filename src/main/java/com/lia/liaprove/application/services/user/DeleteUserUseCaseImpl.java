package com.lia.liaprove.application.services.user;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.AuthorizationException;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.exceptions.UserNotFoundException;
import com.lia.liaprove.core.usecases.user.users.DeleteUserUseCase;

import java.util.Objects;
import java.util.UUID;

/**
 * Implementação do caso de uso "DeleteUserUseCase".
 * <p>
 * Observações:
 * - Depende apenas da porta (gateway): UserGateway.
 * - Garante que o usuário existe antes de tentar deletá-lo.
 * - Contém a lógica de autorização para a exclusão.
 */
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {

    private final UserGateway userGateway;

    public DeleteUserUseCaseImpl(UserGateway userGateway) {
        this.userGateway = Objects.requireNonNull(userGateway, "userGateway must not be null");
    }

    @Override
    public void delete(UUID targetUserId, UUID actorUserId) {
        Objects.requireNonNull(targetUserId, "targetUserId must not be null");
        Objects.requireNonNull(actorUserId, "actorUserId must not be null");

        User targetUser = userGateway.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User to be deleted not found with id: " + targetUserId));

        User actorUser = userGateway.findById(actorUserId)
                .orElseThrow(() -> new UserNotFoundException("Actor user not found with id: " + actorUserId));

        boolean isSelfDelete = targetUserId.equals(actorUserId);
        boolean isActorAdmin = actorUser.getRole() == UserRole.ADMIN;

        // Rule: Admin cannot delete their own account using this flow
        if (isSelfDelete && isActorAdmin) {
            throw new AuthorizationException("Admins cannot delete their own account.");
        }

        // Rule: A user can only delete another if they are admin
        if (!isSelfDelete && !isActorAdmin) {
            throw new AuthorizationException("A user is not allowed to delete another user.");
        }

        // Rule: Admin cannot delete another Admin through this flow
        if (targetUser.getRole() == UserRole.ADMIN && !isSelfDelete) {
            throw new AuthorizationException("Admins are not allowed to delete other admin accounts.");
        }

        userGateway.deleteById(targetUserId);
    }
}
