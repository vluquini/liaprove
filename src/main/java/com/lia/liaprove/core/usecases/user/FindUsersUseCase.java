package com.lia.liaprove.core.usecases.user;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;

import java.util.List;

/**
 * Caso de uso para localizar usuários com base em filtros e paginação.
 * Retorna uma lista de entidades {@code User}.
 */
public interface FindUsersUseCase {
    List<User> findByName(String name, UserRole role, int page, int size);
}
