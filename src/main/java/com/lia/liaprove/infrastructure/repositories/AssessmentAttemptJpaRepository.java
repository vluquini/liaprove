package com.lia.liaprove.infrastructure.repositories;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
        JOIN FETCH a.assessment ass
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
