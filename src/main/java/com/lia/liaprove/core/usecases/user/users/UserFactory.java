package com.lia.liaprove.core.usecases.user.users;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserCreateDto;

/**
 * Interface Factory para a criação de diferentes tipos de usuários.
 */
public interface UserFactory {
    /**
     * Cria uma entidade User (concreta) a partir do comando de criação.
     * A factory gera id, datas e defaults. Lança IllegalArgumentException se o comando for inválido.
     */
    User create(UserCreateDto dto);
}
