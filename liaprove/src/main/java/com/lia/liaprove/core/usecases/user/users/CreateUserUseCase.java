package com.lia.liaprove.core.usecases.user.users;

import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.InvalidUserDataException;

import java.util.UUID;
/**
 * Registra um novo usuário (professional ou recruiter).
 * Retorna o id criado para o usuário.
 */
public interface CreateUserUseCase {
    UUID create(String name, String email, String rawPassword, String occupation, ExperienceLevel experienceLevel,
                  UserRole role) throws InvalidUserDataException;
}
