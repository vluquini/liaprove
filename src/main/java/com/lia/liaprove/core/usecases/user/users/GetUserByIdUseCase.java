package com.lia.liaprove.core.usecases.user.users;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.exceptions.UserNotFoundException;

import java.util.UUID;

public interface GetUserByIdUseCase {
    User findById(UUID userId) throws UserNotFoundException;
}
