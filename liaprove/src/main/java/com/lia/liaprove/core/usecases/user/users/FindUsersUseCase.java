package com.lia.liaprove.core.usecases.user.users;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * Caso de uso para localizar usuários com base em filtros e paginação.
 * Retorna uma lista de entidades {@code User}.
 */
public interface FindUsersUseCase {
    List<User> findByName(Optional<String> name, Optional<UserRole> role, int page, int size);
}
