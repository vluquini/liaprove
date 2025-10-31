package com.lia.liaprove.core.usecases.user.users;

import com.lia.liaprove.core.domain.user.ExperienceLevel;

import java.util.UUID;

/**
 * Caso de uso para atualizar informações do perfil de um usuário (ex: bio, ocupação).
 * <p>
 * Pode lançar as seguintes exceções (unchecked):
 * - {@code UserNotFoundException} se o usuário não for encontrado.
 * - {@code InvalidUserDataException} se os dados fornecidos forem inválidos.
 */
public interface UpdateUserProfileUseCase {
    void updateProfile(UUID userId, String occupation, String bio, ExperienceLevel experienceLevel);
}
