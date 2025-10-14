package com.lia.liaprove.core.usecases.user.admin;

import com.lia.liaprove.core.domain.user.UserRole;

import java.util.UUID;

public interface ChangeUserRoleUseCase {
    void changeRole(UUID userId, UserRole newRole, UUID adminId);
}

