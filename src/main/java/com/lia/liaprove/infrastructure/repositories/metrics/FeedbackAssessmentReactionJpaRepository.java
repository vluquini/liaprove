package com.lia.liaprove.infrastructure.repositories.metrics;

import com.lia.liaprove.infrastructure.entities.metrics.FeedbackAssessmentReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackAssessmentReactionJpaRepository extends JpaRepository<FeedbackAssessmentReactionEntity, UUID> {
    List<FeedbackAssessmentReactionEntity> findByFeedbackAssessmentId(UUID feedbackAssessmentId);
}
