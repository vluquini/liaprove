package com.lia.liaprove.core.usecases.user;

import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.exceptions.InvalidUserDataException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;

import java.util.UUID;

/**
 * Atualiza perfil de usuário — operação de negócio (não lida com autenticação).
 */
public interface UpdateUserProfileUseCase {
    void updateProfile(UUID userId, String occupation, String bio,
                       ExperienceLevel experienceLevel) throws UserNotFoundException, InvalidUserDataException;

}
