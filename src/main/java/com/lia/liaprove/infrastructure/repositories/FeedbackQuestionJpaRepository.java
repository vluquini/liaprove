package com.lia.liaprove.infrastructure.repositories;

import com.lia.liaprove.infrastructure.entities.metrics.FeedbackQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackQuestionJpaRepository extends JpaRepository<FeedbackQuestionEntity, UUID> {

    @Query("SELECT f FROM FeedbackQuestionEntity f JOIN FETCH f.user u LEFT JOIN FETCH f.reactions r WHERE f.question.id = :questionId")
    List<FeedbackQuestionEntity> findWithDetailsByQuestionId(@Param("questionId") UUID questionId);
}
