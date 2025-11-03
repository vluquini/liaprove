package com.lia.liaprove.application.services.user;

import com.lia.liaprove.application.gateways.user.PasswordHasher;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.user.*;
import com.lia.liaprove.core.exceptions.InvalidUserDataException;
import com.lia.liaprove.core.usecases.user.users.CreateUserUseCase;

import java.util.UUID;

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
    private final UserFactory userFactory; // nova dependência

    public CreateUserUseCaseImpl(UserGateway userGateway, PasswordHasher passwordHasher, UserFactory userFactory) {
        this.userGateway = userGateway;
        this.passwordHasher = passwordHasher;
        this.userFactory = userFactory;
    }

    @Override
    public UUID create(String name,
                       String email,
                       String rawPassword,
                       String occupation,
                       ExperienceLevel experienceLevel,
                       UserRole role) throws InvalidUserDataException {

        validateBasic(name, email, rawPassword, role);

        // Verificar unicidade do email
        userGateway.findByEmail(email).ifPresent(existingUser -> {
            throw new InvalidUserDataException("Email already registered: " + existingUser.getEmail());
        });

        // Hash da senha (infra)
        String passwordHash = passwordHasher.hash(rawPassword);

        // Criar comando e delegar para a factory (application cria a factory impl)
        UserCreateDto dto = new UserCreateDto(name, email, passwordHash, occupation, experienceLevel, role);
        User user = userFactory.create(dto);

        // Persistir via gateway
        userGateway.save(user);

        return user.getId();
    }

    // validateBasic permanece igual...

    private void validateBasic(String name, String email, String rawPassword, UserRole role) throws InvalidUserDataException {
        if (name == null || name.isBlank()) {
            throw new InvalidUserDataException("Name must not be empty");
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new InvalidUserDataException("Invalid email");
        }
        if (rawPassword == null || rawPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new InvalidUserDataException("Password must have at least " + MIN_PASSWORD_LENGTH + " characters");
        }
        if (role == null) {
            throw new InvalidUserDataException("Role must be provided");
        }
    }
}
