package com.lia.liaprove.infrastructure.services.user;

import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.mappers.user.UserMapper;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;
import java.util.stream.Collectors;

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
        return userRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> search(String name, UserRole role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<UserEntity> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (role != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("role"), role));
        }

        return userRepository.findAll(spec, pageable).getContent().stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Map<UUID, User> findByIdsAsMap(Collection<UUID> ids) {
        return userRepository.findAllById(ids).stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toMap(User::getId, user -> user));
    }

    @Override
    public List<User> saveAll(Collection<User> users) {
        List<UserEntity> userEntities = users.stream()
                .map(userMapper::toEntity)
                .collect(Collectors.toList());

        return userRepository.saveAll(userEntities).stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }
}
