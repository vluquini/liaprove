package com.lia.liaprove.infrastructure.repositories.assessment;

import com.lia.liaprove.infrastructure.entities.metrics.AssessmentAttemptVoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssessmentAttemptVoteJpaRepository extends JpaRepository<AssessmentAttemptVoteEntity, UUID> {
    boolean existsByUserIdAndAssessmentAttemptId(UUID userId, UUID assessmentAttemptId);
}
