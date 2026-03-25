package com.lia.liaprove.infrastructure.repositories;

import com.lia.liaprove.infrastructure.entities.metrics.FeedbackQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.lia.liaprove.core.domain.metrics.ReactionType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeedbackQuestionJpaRepository extends JpaRepository<FeedbackQuestionEntity, UUID> {

    @Query("SELECT f FROM FeedbackQuestionEntity f JOIN FETCH f.user feedbackUser LEFT JOIN FETCH f.reactions reaction " +
            "LEFT JOIN FETCH reaction.user reactionUser WHERE f.question.id = :questionId")
    List<FeedbackQuestionEntity> findWithDetailsByQuestionId(@Param("questionId") UUID questionId);

    @Query("SELECT f FROM FeedbackQuestionEntity f JOIN FETCH f.user feedbackUser JOIN FETCH f.question " +
            "LEFT JOIN FETCH f.reactions reaction LEFT JOIN FETCH reaction.user reactionUser WHERE f.id = :feedbackId")
    Optional<FeedbackQuestionEntity> findFeedbackByIdWithDetails(@Param("feedbackId") UUID feedbackId);

    @Query("""
        SELECT f.user.id,
               SUM(CASE WHEN r.type = :like THEN 1 ELSE 0 END),
               SUM(CASE WHEN r.type = :dislike THEN 1 ELSE 0 END)
        FROM FeedbackQuestionEntity f
        LEFT JOIN f.reactions r
        GROUP BY f.user.id
    """)
    List<Object[]> countReactionsByFeedbackAuthor(@Param("like") ReactionType like,
                                                  @Param("dislike") ReactionType dislike);
}
