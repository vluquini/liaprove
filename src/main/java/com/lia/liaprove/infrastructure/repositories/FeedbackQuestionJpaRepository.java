package com.lia.liaprove.infrastructure.repositories;

import com.lia.liaprove.infrastructure.entities.metrics.FeedbackQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedbackQuestionJpaRepository extends JpaRepository<FeedbackQuestionEntity, UUID> {

    @Query("SELECT f FROM FeedbackQuestionEntity f JOIN FETCH f.user feedbackUser LEFT JOIN FETCH f.reactions reaction LEFT JOIN FETCH reaction.user reactionUser WHERE f.question.id = :questionId")
    List<FeedbackQuestionEntity> findWithDetailsByQuestionId(@Param("questionId") UUID questionId);

    @Query("SELECT f FROM FeedbackQuestionEntity f JOIN FETCH f.user JOIN FETCH f.question LEFT JOIN FETCH f.reactions r WHERE f.id = :feedbackId")
    Optional<FeedbackQuestionEntity> findFeedbackByIdWithDetails(@Param("feedbackId") UUID feedbackId);
}
