package com.lia.liaprove.application.services.user;

import com.lia.liaprove.application.gateways.user.PasswordHasher;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.user.*;
import com.lia.liaprove.core.exceptions.InvalidUserDataException;
import com.lia.liaprove.core.usecases.user.users.CreateUserUseCase;
import com.lia.liaprove.core.usecases.user.users.UserFactory;

/**
 * Implementação do caso de uso "CreateUser".
 *
 * Observações:
 * - Depende apenas de portas (gateways): UserGateway e PasswordHasher.
 * - A lógica de criação de entidades User foi delegada para a UserFactory.
 */
public class CreateUserUseCaseImpl implements CreateUserUseCase {
    private static final int MIN_PASSWORD_LENGTH = 6;

    private final UserGateway userGateway;
    private final PasswordHasher passwordHasher;
    private final UserFactory userFactory;

    public CreateUserUseCaseImpl(UserGateway userGateway, PasswordHasher passwordHasher, UserFactory userFactory) {
        this.userGateway = userGateway;
        this.passwordHasher = passwordHasher;
        this.userFactory = userFactory;
    }

    @Override
    public User create(String name, String email, String rawPassword,
                       String occupation, ExperienceLevel experienceLevel, UserRole role) {

        validatePassword(rawPassword);

        // Verificar unicidade do email
        userGateway.findByEmail(email).ifPresent(existingUser -> {
            throw new InvalidUserDataException("Email already registered: " + existingUser.getEmail());
        });

        // Hash da senha (infra)
        String passwordHash = passwordHasher.hash(rawPassword);

        // Criar comando e delegar para a factory (application cria a factory impl)
        UserCreateDto dto = new UserCreateDto(name, email, passwordHash, occupation, experienceLevel, role);
        User user = userFactory.create(dto);

        // Persistir via gateway e retornar o usuário salvo
        return userGateway.save(user);
    }

    private void validatePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new InvalidUserDataException("Password must have at least " + MIN_PASSWORD_LENGTH + " characters");
        }
    }
}
