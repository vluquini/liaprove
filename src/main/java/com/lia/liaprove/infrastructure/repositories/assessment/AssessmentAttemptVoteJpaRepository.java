package com.lia.liaprove.infrastructure.repositories.assessment;

import com.lia.liaprove.infrastructure.entities.metrics.AssessmentAttemptVoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssessmentAttemptVoteJpaRepository extends JpaRepository<AssessmentAttemptVoteEntity, UUID> {
    boolean existsByUserIdAndAssessmentAttemptId(UUID userId, UUID assessmentAttemptId);

    @Query("""
        SELECT v
        FROM AssessmentAttemptVoteEntity v
        JOIN FETCH v.user u
        JOIN FETCH v.assessmentAttempt a
        WHERE a.id = :attemptId
    """)
    List<AssessmentAttemptVoteEntity> findByAssessmentAttemptIdWithDetails(@Param("attemptId") UUID attemptId);
}
