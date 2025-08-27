package com.lia.liaprove.core.usecases.user;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * Busca usuários por critérios simples.
 * Implementação pode suportar paginação; assinatura simples.
 */
public interface FindUsersUseCase {
    List<User> findByName(Optional<String> name, Optional<UserRole> role, int page, int size);
}
