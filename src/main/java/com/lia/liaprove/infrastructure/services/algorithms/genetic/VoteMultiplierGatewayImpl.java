package com.lia.liaprove.infrastructure.services.algorithms.genetic;

import com.lia.liaprove.application.gateways.algorithms.genetic.VoteMultiplierGateway;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.infrastructure.entities.algorithms.genetic.VoteMultiplierEntity;
import com.lia.liaprove.infrastructure.entities.algorithms.genetic.VoteMultiplierScope;
import com.lia.liaprove.infrastructure.repositories.VoteMultiplierJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class VoteMultiplierGatewayImpl implements VoteMultiplierGateway {

    private final VoteMultiplierJpaRepository voteMultiplierJpaRepository;

    public VoteMultiplierGatewayImpl(VoteMultiplierJpaRepository voteMultiplierJpaRepository) {
        this.voteMultiplierJpaRepository = voteMultiplierJpaRepository;
    }

    @Override
    @Transactional
    public void setRoleMultiplier(UserRole role, double multiplier) {
        VoteMultiplierEntity entity = voteMultiplierJpaRepository
                .findByScopeAndRole(VoteMultiplierScope.ROLE, role)
                .orElseGet(() -> createRoleEntity(role));

        entity.setMultiplier(multiplier);
        entity.setUpdatedAt(LocalDateTime.now());
        voteMultiplierJpaRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Double> getRoleMultiplier(UserRole role) {
        return voteMultiplierJpaRepository
                .findByScopeAndRole(VoteMultiplierScope.ROLE, role)
                .map(VoteMultiplierEntity::getMultiplier);
    }

    @Override
    @Transactional
    public void setRecruiterMultiplier(UUID recruiterId, double multiplier) {
        VoteMultiplierEntity entity = voteMultiplierJpaRepository
                .findByScopeAndRecruiterId(VoteMultiplierScope.RECRUITER, recruiterId)
                .orElseGet(() -> createRecruiterEntity(recruiterId));

        entity.setMultiplier(multiplier);
        entity.setUpdatedAt(LocalDateTime.now());
        voteMultiplierJpaRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Double> getRecruiterMultiplier(UUID recruiterId) {
        return voteMultiplierJpaRepository
                .findByScopeAndRecruiterId(VoteMultiplierScope.RECRUITER, recruiterId)
                .map(VoteMultiplierEntity::getMultiplier);
    }

    private VoteMultiplierEntity createRoleEntity(UserRole role) {
        VoteMultiplierEntity entity = new VoteMultiplierEntity();
        entity.setScope(VoteMultiplierScope.ROLE);
        entity.setRole(role);
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    private VoteMultiplierEntity createRecruiterEntity(UUID recruiterId) {
        VoteMultiplierEntity entity = new VoteMultiplierEntity();
        entity.setScope(VoteMultiplierScope.RECRUITER);
        entity.setRecruiterId(recruiterId);
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
}
