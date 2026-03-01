package com.lia.liaprove.infrastructure.repositories;

import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssessmentAttemptJpaRepository extends JpaRepository<AssessmentAttemptEntity, UUID> {
    List<AssessmentAttemptEntity> findByAssessmentId(UUID assessmentId);

    long countByAssessmentId(UUID assessmentId);

    boolean existsByAssessmentIdAndUserId(UUID assessmentId, UUID userId);
}

