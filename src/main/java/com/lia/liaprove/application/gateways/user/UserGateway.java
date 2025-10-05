package com.lia.liaprove.application.gateways.user;

import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta (gateway) para operações de persistência/consulta da entidade User.
 */
public interface UserGateway {
    //Persiste (insere/atualiza) o usuário.
    void save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    void deleteById(UUID id);
    List<User> search(Optional<String> name, Optional<UserRole> role, int page, int size);
}
