package com.lia.liaprove.infrastructure.repositories.metrics;

import com.lia.liaprove.infrastructure.entities.metrics.QuestionVoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VoteJpaRepository extends JpaRepository<QuestionVoteEntity, UUID> {
    @Query("SELECT v FROM QuestionVoteEntity v JOIN FETCH v.user JOIN FETCH v.question q LEFT JOIN FETCH TREAT(q AS MultipleChoiceQuestionEntity).alternatives a WHERE v.question.id = :questionId")
    List<QuestionVoteEntity> findWithDetailsByQuestionId(@Param("questionId") UUID questionId);

    @Query("""
            SELECT DISTINCT v
            FROM QuestionVoteEntity v
            JOIN FETCH v.user
            JOIN FETCH v.question q
            LEFT JOIN FETCH TREAT(q AS MultipleChoiceQuestionEntity).alternatives a
            WHERE v.user.id = :userId
              AND v.question.id = :questionId
            """)
    List<QuestionVoteEntity> findByUserIdAndQuestionId(@Param("userId") UUID userId, @Param("questionId") UUID questionId);
}
