package com.lia.liaprove.application.services.user;

import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.domain.user.UserStatus;
import com.lia.liaprove.core.exceptions.AuthorizationException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;
import com.lia.liaprove.core.usecases.user.admin.UserModerationUseCase;

import java.util.Objects;
import java.util.UUID;

public class UserModerationUseCaseImpl implements UserModerationUseCase {

    private final UserGateway userGateway;

    public UserModerationUseCaseImpl(UserGateway userGateway) {
        this.userGateway = Objects.requireNonNull(userGateway, "userGateway must not be null");
    }

    @Override
    public void activateUser(UUID targetUserId, UUID actorId) {
        moderate(targetUserId, actorId, UserStatus.ACTIVE);
    }

    @Override
    public void deactivateUser(UUID targetUserId, UUID actorId) {
        moderate(targetUserId, actorId, UserStatus.INACTIVE);
    }

    private void moderate(UUID targetUserId, UUID actorId, UserStatus newStatus) {
        Objects.requireNonNull(targetUserId, "targetUserId must not be null");
        Objects.requireNonNull(actorId, "actorId must not be null");

        User targetUser = userGateway.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + targetUserId));

        // Regra: Usuário pode desativar a própria conta
        if (targetUserId.equals(actorId) && newStatus == UserStatus.INACTIVE) {
            targetUser.setStatus(UserStatus.INACTIVE);
            userGateway.save(targetUser);
            return;
        }

        // Regra: Para outras operações, deve ser ADMIN
        User actor = userGateway.findById(actorId)
                .orElseThrow(() -> new UserNotFoundException("Actor user not found with id: " + actorId));

        if (actor.getRole() != UserRole.ADMIN) {
            throw new AuthorizationException("Only admins can moderate other users.");
        }

        if (targetUserId.equals(actorId)) {
            throw new AuthorizationException("Admins cannot moderate their own status through this channel.");
        }

        targetUser.setStatus(newStatus);
        userGateway.save(targetUser);
    }
}
