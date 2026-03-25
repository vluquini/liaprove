package com.lia.liaprove.infrastructure.repositories;

import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentEntity;
import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssessmentJpaRepository extends JpaRepository<AssessmentEntity, UUID> {
    Optional<PersonalizedAssessmentEntity> findByShareableToken(String shareableToken);

    @Query("""
        SELECT a
        FROM AssessmentEntity a
        LEFT JOIN FETCH TREAT(a AS PersonalizedAssessmentEntity).createdBy
        WHERE a.id = :id
    """)
    Optional<AssessmentEntity> findByIdWithCreator(@Param("id") UUID id);

    @Query("SELECT pa FROM PersonalizedAssessmentEntity pa WHERE pa.status = :status AND pa.expirationDate < :currentDateTime")
    List<PersonalizedAssessmentEntity> findActiveAssessmentsWithPastExpirationDate(
            @Param("status") PersonalizedAssessmentStatus status,
            @Param("currentDateTime") LocalDateTime currentDateTime
    );

    @Query("SELECT pa.createdBy.id, COUNT(pa) " +
            "FROM PersonalizedAssessmentEntity pa " +
            "GROUP BY pa.createdBy.id")
    List<Object[]> countAssessmentsByRecruiter();

    @Query("SELECT pa.createdBy.id, COUNT(pa) " +
            "FROM PersonalizedAssessmentEntity pa " +
            "WHERE pa.creationDate >= :since " +
            "GROUP BY pa.createdBy.id")
    List<Object[]> countRecentAssessmentsByRecruiter(@Param("since") LocalDateTime since);
}

