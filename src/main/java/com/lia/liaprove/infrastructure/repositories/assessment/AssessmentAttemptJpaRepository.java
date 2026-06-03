package com.lia.liaprove.infrastructure.repositories.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.infrastructure.entities.assessment.CertificateEntity;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssessmentAttemptJpaRepository extends JpaRepository<AssessmentAttemptEntity, UUID>,
        JpaSpecificationExecutor<AssessmentAttemptEntity> {
    List<AssessmentAttemptEntity> findByAssessmentId(UUID assessmentId);

    @Query("""
        SELECT a
        FROM AssessmentAttemptEntity a
        JOIN FETCH a.user u
        JOIN FETCH a.assessment ass
        WHERE ass.id = :assessmentId
    """)
    List<AssessmentAttemptEntity> findSummariesByAssessmentId(@Param("assessmentId") UUID assessmentId);

    long countByAssessmentId(UUID assessmentId);

    boolean existsByAssessmentIdAndUserId(UUID assessmentId, UUID userId);

    @Query("""
        SELECT a
        FROM AssessmentAttemptEntity a
        LEFT JOIN FETCH a.assessment ass
        WHERE a.id = :id
    """)
    Optional<AssessmentAttemptEntity> findByIdFetchingAssessment(@Param("id") UUID id);

    @Query("""
        SELECT a
        FROM AssessmentAttemptEntity a
        LEFT JOIN FETCH a.assessment ass
        LEFT JOIN FETCH TREAT(ass AS PersonalizedAssessmentEntity).createdBy
        WHERE a.id = :id
    """)
    Optional<AssessmentAttemptEntity> findByIdWithAssessmentAndCreator(@Param("id") UUID id);

    @Query("""
        SELECT a
        FROM AssessmentAttemptEntity a
        JOIN FETCH a.user u
        JOIN FETCH a.certificate c
        WHERE c.certificateNumber = :certificateNumber
    """)
    Optional<AssessmentAttemptEntity> findByCertificateNumber(@Param("certificateNumber") String certificateNumber);

    @Query("""
        SELECT c
        FROM AssessmentAttemptEntity a
        JOIN a.certificate c
        WHERE a.user.id = :userId
        ORDER BY c.issueDate DESC, c.certificateNumber ASC
    """)
    List<CertificateEntity> findCertificatesByUserId(@Param("userId") UUID userId);

    @Query("""
        SELECT a
        FROM AssessmentAttemptEntity a
        JOIN FETCH a.user u
        JOIN FETCH a.assessment ass
        JOIN FETCH a.certificate c
        WHERE u.id = :userId
        ORDER BY c.issueDate DESC, c.certificateNumber ASC
    """)
    List<AssessmentAttemptEntity> findCertifiedAttemptsByUserId(@Param("userId") UUID userId);

    @Query("""
        SELECT a
        FROM AssessmentAttemptEntity a
        JOIN FETCH a.user u
        JOIN FETCH a.assessment ass
        JOIN FETCH a.certificate c
        WHERE u.id = :userId
          AND TYPE(ass) = SystemAssessmentEntity
          AND TREAT(ass AS SystemAssessmentEntity).knowledgeArea = :knowledgeArea
          AND TREAT(ass AS SystemAssessmentEntity).difficultyLevel = :difficultyLevel
        ORDER BY c.score DESC, c.issueDate DESC, c.certificateNumber ASC
    """)
    List<AssessmentAttemptEntity> findCertifiedSystemAttemptsByUserAndCriteria(
            @Param("userId") UUID userId,
            @Param("knowledgeArea") KnowledgeArea knowledgeArea,
            @Param("difficultyLevel") DifficultyLevel difficultyLevel,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT a
        FROM AssessmentAttemptEntity a
        JOIN FETCH a.user u
        JOIN FETCH a.assessment ass
        JOIN FETCH a.answers ans
        WHERE TYPE(ass) = SystemAssessmentEntity
          AND a.finishedAt IS NOT NULL
          AND u.id <> :userId
          AND ans.projectUrl IS NOT NULL
          AND ans.projectUrl <> ''
        ORDER BY a.finishedAt DESC
    """)
    List<AssessmentAttemptEntity> findPublicSystemProjectAttemptsExcludingUser(@Param("userId") UUID userId);

    @Query("""
        SELECT DISTINCT a
        FROM AssessmentAttemptEntity a
        JOIN FETCH a.user u
        JOIN FETCH a.assessment ass
        JOIN FETCH a.answers ans
        WHERE a.id = :attemptId
          AND TYPE(ass) = SystemAssessmentEntity
          AND a.finishedAt IS NOT NULL
          AND u.id <> :userId
          AND (
            (ans.projectUrl IS NOT NULL AND ans.projectUrl <> '')
            OR (ans.textResponse IS NOT NULL AND ans.textResponse <> '')
          )
    """)
    Optional<AssessmentAttemptEntity> findPublicSystemProjectAttemptDetailsExcludingUser(
            @Param("attemptId") UUID attemptId,
            @Param("userId") UUID userId
    );

    @Query("""
        SELECT DISTINCT a
        FROM AssessmentAttemptEntity a
        JOIN FETCH a.assessment ass
        JOIN FETCH a.user u
        JOIN FETCH a.answers ans
        WHERE TYPE(ass) = SystemAssessmentEntity
          AND a.status = AssessmentAttemptStatus.COMPLETED
          AND a.finishedAt IS NOT NULL
          AND a.finishedAt <= :cutoff
          AND ans.projectUrl IS NOT NULL
          AND ans.projectUrl <> ''
    """)
    List<AssessmentAttemptEntity> findCompletedSystemProjectAttemptsReadyForCommunityDecision(@Param("cutoff") LocalDateTime cutoff);

    @Query("""
        SELECT DISTINCT a
        FROM AssessmentAttemptEntity a
        JOIN FETCH a.assessment ass
        JOIN FETCH a.user u
        JOIN FETCH a.answers ans
        WHERE TYPE(ass) = SystemAssessmentEntity
          AND a.status = AssessmentAttemptStatus.COMPLETED
          AND a.finishedAt IS NOT NULL
          AND ans.projectUrl IS NOT NULL
          AND ans.projectUrl <> ''
    """)
    List<AssessmentAttemptEntity> findCompletedSystemProjectAttemptsReadyForDemoCommunityDecision();

    @Query("""
        SELECT pa.createdBy.id, AVG(a.accuracyRate)
        FROM AssessmentAttemptEntity a
        JOIN a.assessment ass
        JOIN TREAT(ass AS PersonalizedAssessmentEntity) pa
        WHERE a.accuracyRate IS NOT NULL
          AND a.status IN :finalStatuses
        GROUP BY pa.createdBy.id
    """)
    List<Object[]> avgAccuracyByRecruiter(@Param("finalStatuses") List<AssessmentAttemptStatus> finalStatuses);
}
