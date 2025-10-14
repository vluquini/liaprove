package com.lia.liaprove.core.usecases.user.admin;

import java.util.UUID;

public interface UserModerationUseCase {
    void activateUser(UUID userId, UUID adminId);
    void deactivateUser(UUID userId, UUID adminId);
}

