package com.lia.liaprove.infrastructure.services;

import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.infrastructure.mappers.UserMapper;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;

import java.util.*;

public class UserGatewayImpl implements UserGateway {

    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;

    public UserGatewayImpl(UserJpaRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        return userMapper.toDomain(userRepository.save(userMapper.toEntity(user)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteById(UUID id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<User> search(Optional<String> name, Optional<UserRole> role, int page, int size) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Map<UUID, User> findByIdsAsMap(Collection<UUID> ids) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<User> saveAll(Collection<User> users) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
