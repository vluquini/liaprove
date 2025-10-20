package com.lia.liaprove.application.gateways.user;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;

import java.util.*;

/**
 * Porta (gateway) para operações de persistência/consulta da entidade User.
 */
public interface UserGateway {
    // Persiste (insere/atualiza) o usuário.
    void save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    void deleteById(UUID id);
    List<User> search(Optional<String> name, Optional<UserRole> role, int page, int size);

    /**
     * Busca usuários por um conjunto de ids retornando um mapa id->User.
     */
    Map<UUID, User> findByIdsAsMap(Collection<UUID> ids);

    /**
     * Persiste/atualiza em lote e retorna as entidades persistidas.
     * Retornar a lista facilita verificações testabilidade.
     */
    List<User> saveAll(Collection<User> users);
}