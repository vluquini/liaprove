package com.lia.liaprove.infrastructure.repositories;

import com.lia.liaprove.infrastructure.entities.metrics.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VoteJpaRepository extends JpaRepository<VoteEntity, UUID> {
    List<VoteEntity> findByQuestionId(UUID questionId);
}
