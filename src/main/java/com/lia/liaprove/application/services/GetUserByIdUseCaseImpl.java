package com.lia.liaprove.application.services;

import com.lia.liaprove.application.gateways.UserGateway;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.exceptions.UserNotFoundException;
import com.lia.liaprove.core.usecases.user.GetUserByIdUseCase;

import java.util.Objects;
import java.util.UUID;

public class GetUserByIdUseCaseImpl implements GetUserByIdUseCase {

    private final UserGateway userGateway;

    public GetUserByIdUseCaseImpl(UserGateway userGateway) {
        this.userGateway = Objects.requireNonNull(userGateway, "userGateway must not be null");
    }

    @Override
    public User findById(UUID userId) throws UserNotFoundException {
        Objects.requireNonNull(userId, "userId must not be null");

        return userGateway.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }
}
