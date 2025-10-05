package com.lia.liaprove.application.services.user;

import com.lia.liaprove.application.gateways.user.PasswordHasher;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.domain.user.*;
import com.lia.liaprove.core.exceptions.InvalidUserDataException;
import com.lia.liaprove.core.usecases.user.CreateUserUseCase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do caso de uso "CreateUser".
 *
 * Observações:
 * - Depende apenas de portas (gateways): UserGateway e PasswordHasher.
 * - Validações básicas serão executadas aqui. Se necessário, validações mais complexas serão extraídas para helpers/validators.
 */
public class CreateUserUseCaseImpl implements CreateUserUseCase {
    private static final int MIN_PASSWORD_LENGTH = 6;

    private final UserGateway userGateway;
    private final PasswordHasher passwordHasher;

    public CreateUserUseCaseImpl(UserGateway userGateway, PasswordHasher passwordHasher) {
        this.userGateway = userGateway;
        this.passwordHasher = passwordHasher;
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
        Optional<User> existing = userGateway.findByEmail(email);
        if (existing.isPresent()) {
            throw new InvalidUserDataException("Email already registered: " + email);
        }

        // Hash da senha
        String passwordHash = passwordHasher.hash(rawPassword);

        // Criar entidade de domínio (Professional ou Recruiter)
        User user;
        if (role == UserRole.PROFESSIONAL) {
            // Criando UserProfessional
            user = createProfessional(name, email, passwordHash, occupation, experienceLevel);
        } else if (role == UserRole.RECRUITER) {
            // Criando UserRecruiter
            user = createRecruiter(name, email, passwordHash, occupation);
        } else if (role == UserRole.ADMIN) {
            // Tratar ADMIN como recruiter genérico
            user = createRecruiter(name, email, passwordHash, occupation);
            user.setRole(UserRole.ADMIN);
        } else {
            throw new InvalidUserDataException("Unsupported role: " + role);
        }

        // Persistir via gateway
        userGateway.save(user);

        return user.getId();
    }

    // Helpers

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

    private User createProfessional(String name, String email, String passwordHash, String occupation, ExperienceLevel experienceLevel) {
        UserProfessional p = new UserProfessional();
        p.setId(UUID.randomUUID());
        p.setName(name.trim());
        p.setEmail(email.trim().toLowerCase());
        p.setPasswordHash(passwordHash);
        p.setOccupation(occupation == null ? "" : occupation.trim());
        p.setBio("");
        p.setExperienceLevel(experienceLevel == null ? ExperienceLevel.JUNIOR : experienceLevel);
        p.setRole(UserRole.PROFESSIONAL);
        p.setVoteWeight(0);
        p.setTotalAssessmentsTaken(0);
        p.setCertificates(new ArrayList<Certificate>());
        p.setAverageScore(0f);
        p.setRegistrationDate(LocalDateTime.now());
        p.setLastLogin(null);
        return p;
    }

    private User createRecruiter(String name, String email, String passwordHash, String occupation) {
        UserRecruiter r = new UserRecruiter();
        r.setId(UUID.randomUUID());
        r.setName(name.trim());
        r.setEmail(email.trim().toLowerCase());
        r.setPasswordHash(passwordHash);
        r.setOccupation(occupation == null ? "" : occupation.trim());
        r.setBio("");
        r.setRole(UserRole.RECRUITER);
        r.setVoteWeight(0);
        r.setTotalAssessmentsTaken(0);
        r.setCertificates(new ArrayList<Certificate>());
        r.setAverageScore(0f);
        r.setRegistrationDate(LocalDateTime.now());
        r.setLastLogin(null);
        // campos específicos de recruiter (companyName/companyEmail) podem ser atualizados depois
        return r;
    }

}
