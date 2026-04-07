package com.lia.liaprove.infrastructure.repositories;

import com.lia.liaprove.infrastructure.entities.metrics.FeedbackAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedbackAssessmentJpaRepository extends JpaRepository<FeedbackAssessmentEntity, UUID> {
    boolean existsByUserIdAndAssessmentAttemptId(UUID userId, UUID assessmentAttemptId);
}
