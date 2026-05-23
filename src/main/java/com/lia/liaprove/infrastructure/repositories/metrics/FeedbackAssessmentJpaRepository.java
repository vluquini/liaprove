package com.lia.liaprove.infrastructure.repositories.metrics;

import com.lia.liaprove.infrastructure.entities.metrics.FeedbackAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedbackAssessmentJpaRepository extends JpaRepository<FeedbackAssessmentEntity, UUID> {
    boolean existsByUserIdAndAssessmentAttemptId(UUID userId, UUID assessmentAttemptId);

    @Query("""
        SELECT DISTINCT f
        FROM FeedbackAssessmentEntity f
        JOIN FETCH f.user u
        LEFT JOIN FETCH f.reactions r
        LEFT JOIN FETCH r.user ru
        WHERE f.assessmentAttemptId = :attemptId
          AND f.visible = true
        ORDER BY f.submissionDate ASC
    """)
    List<FeedbackAssessmentEntity> findVisibleByAssessmentAttemptIdWithDetails(@Param("attemptId") UUID attemptId);

    @Query("""
        SELECT DISTINCT f
        FROM FeedbackAssessmentEntity f
        JOIN FETCH f.user u
        LEFT JOIN FETCH f.reactions r
        LEFT JOIN FETCH r.user ru
        WHERE f.id = :feedbackId
    """)
    Optional<FeedbackAssessmentEntity> findByIdWithDetails(@Param("feedbackId") UUID feedbackId);
}
