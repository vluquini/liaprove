package com.lia.liaprove.application.services.user;

import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.usecases.user.FindUsersUseCase;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementação simples do caso de uso "FindUsers".
 *
 * Observação prática:
 * - Esta implementação delega a lógica de consulta ao UserGateway.
 */
public class FindUsersUseCaseImpl implements FindUsersUseCase {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    private final UserGateway userGateway;

    public FindUsersUseCaseImpl(UserGateway userGateway) {
        this.userGateway = Objects.requireNonNull(userGateway, "userGateway must not be null");
    }

    @Override
    public List<User> findByName(Optional<String> nameOpt, Optional<UserRole> roleOpt, int page, int size) {
        int p = Math.max(DEFAULT_PAGE, page);
        int s = Math.max(1, size <= 0 ? DEFAULT_SIZE : size);

        // Normaliza parâmetros simples (trim + empty -> empty optional)
        Optional<String> name = nameOpt
                .filter(n -> !n.isBlank())
                .map(String::trim);

        List<User> result = userGateway.search(name, roleOpt, p, s);
        return result == null ? Collections.emptyList() : result;
    }
}
