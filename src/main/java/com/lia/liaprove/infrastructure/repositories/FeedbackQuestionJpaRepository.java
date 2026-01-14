package com.lia.liaprove.infrastructure.repositories;

import com.lia.liaprove.infrastructure.entities.metrics.FeedbackQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackQuestionJpaRepository extends JpaRepository<FeedbackQuestionEntity, UUID> {
    List<FeedbackQuestionEntity> findByQuestionId(UUID questionId);
}
