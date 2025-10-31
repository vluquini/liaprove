package com.lia.liaprove.core.usecases.user.users;

import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRole;

import java.util.UUID;

/**
 * Caso de uso para registrar um novo usuário, seja ele Professional ou Recruiter.
 * <p>
 * Pode lançar as seguintes exceções (unchecked):
 * - {@code InvalidUserDataException} se os dados fornecidos forem inválidos (ex: email duplicado, senha fraca).
 */
public interface CreateUserUseCase {
    UUID create(String name, String email, String rawPassword, String occupation,
                ExperienceLevel experienceLevel, UserRole role);
}
