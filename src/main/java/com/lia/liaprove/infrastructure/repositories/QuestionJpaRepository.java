package com.lia.liaprove.infrastructure.repositories;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface QuestionJpaRepository extends JpaRepository<QuestionEntity, UUID> {
    boolean existsByDescription(String description);

    @Query("SELECT DISTINCT q FROM QuestionEntity q LEFT JOIN q.knowledgeAreas ka " +
           "WHERE ( (:#{#knowledgeAreas == null || #knowledgeAreas.isEmpty()} = true) OR (ka IN (:knowledgeAreas)) ) " +
           "AND (:difficultyByCommunity IS NULL OR q.difficultyByCommunity = :difficultyByCommunity) " +
           "AND (:status IS NULL OR q.status = :status) " +
           "AND (:authorId IS NULL OR q.authorId = :authorId)")
    List<QuestionEntity> findAllWithFilters(
            @Param("knowledgeAreas") Set<KnowledgeArea> knowledgeAreas,
            @Param("difficultyByCommunity") DifficultyLevel difficultyByCommunity,
            @Param("status") QuestionStatus status,
            @Param("authorId") UUID authorId,
            Pageable pageable
    );

    List<QuestionEntity> findByStatusAndVotingEndDateBefore(QuestionStatus status, java.time.LocalDateTime votingEndDate);
}
