package com.lia.liaprove.infrastructure.repositories.metrics;

import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.infrastructure.entities.algorithms.genetic.VoteMultiplierEntity;
import com.lia.liaprove.infrastructure.entities.algorithms.genetic.VoteMultiplierScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoteMultiplierJpaRepository extends JpaRepository<VoteMultiplierEntity, UUID> {
    Optional<VoteMultiplierEntity> findByScopeAndRole(VoteMultiplierScope scope, UserRole role);
    Optional<VoteMultiplierEntity> findByScopeAndRecruiterId(VoteMultiplierScope scope, UUID recruiterId);
}
