package com.lia.liaprove.core.usecases.user;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.exceptions.UserNotFoundException;

import java.util.UUID;

/**
 * Recupera a entidade User pelo id.
 */
public interface GetUserByIdUseCase {
    User findById(UUID userId) throws UserNotFoundException;
}
