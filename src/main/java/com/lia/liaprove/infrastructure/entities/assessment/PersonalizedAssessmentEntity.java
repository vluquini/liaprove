package com.lia.liaprove.infrastructure.entities.assessment;

import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.infrastructure.entities.users.UserRecruiterEntity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("PERSONALIZED")
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonalizedAssessmentEntity extends AssessmentEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_recruiter_id", nullable = false)
    private UserRecruiterEntity createdBy;

    @Column(nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private int totalAttempts;

    @Column(nullable = false)
    private int maxAttempts;

    @Column(nullable = false, unique = true)
    private String shareableToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PersonalizedAssessmentStatus status;
}

