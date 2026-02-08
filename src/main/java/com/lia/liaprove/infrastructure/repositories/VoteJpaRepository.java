package com.lia.liaprove.infrastructure.repositories;

import com.lia.liaprove.infrastructure.entities.metrics.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VoteJpaRepository extends JpaRepository<VoteEntity, UUID> {
    @Query("SELECT v FROM VoteEntity v JOIN FETCH v.user JOIN FETCH v.question q LEFT JOIN FETCH TREAT(q AS MultipleChoiceQuestionEntity).alternatives a WHERE v.question.id = :questionId")
    List<VoteEntity> findWithDetailsByQuestionId(@Param("questionId") UUID questionId);
}
