package com.lia.liaprove.application.services.user;

import com.lia.liaprove.application.gateways.user.PasswordHasher;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.exceptions.InvalidCredentialsException;
import com.lia.liaprove.core.exceptions.InvalidUserDataException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;
import com.lia.liaprove.core.usecases.user.users.ChangePasswordUseCase;

import java.util.Objects;
import java.util.UUID;

/**
 * Implementação do caso de uso "ChangePasswordUseCase".
 *
 * Observações:
 * - Depende apenas de portas (gateways): UserGateway e PasswordHasher.
 * - Inclui validação de senha e verificação da senha antiga.
 */
public class ChangePasswordUseCaseImpl implements ChangePasswordUseCase {
    private static final int MIN_PASSWORD_LENGTH = 6;

    private final UserGateway userGateway;
    private final PasswordHasher passwordHasher;

    public ChangePasswordUseCaseImpl(UserGateway userGateway, PasswordHasher passwordHasher) {
        this.userGateway = Objects.requireNonNull(userGateway, "userGateway must not be null");
        this.passwordHasher = Objects.requireNonNull(passwordHasher, "passwordHasher must not be null");
    }

    @Override
    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        Objects.requireNonNull(userId, "userId must not be null");

        User user = userGateway.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (!passwordHasher.matches(oldPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid old password.");
        }

        validatePassword(newPassword);

        user.setPasswordHash(passwordHasher.hash(newPassword));

        userGateway.save(user);
    }

    private void validatePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new InvalidUserDataException("Password must have at least " + MIN_PASSWORD_LENGTH + " characters");
        }
    }
}
