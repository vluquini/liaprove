package com.lia.liaprove.infrastructure.entities.assessment;

import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.infrastructure.entities.user.UserRecruiterEntity;
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
    @JoinColumn(name = "created_by_recruiter_id")
    private UserRecruiterEntity createdBy;

    @Column
    private LocalDateTime expirationDate;

    @Column
    private int totalAttempts;

    @Column
    private int maxAttempts;

    @Column(unique = true)
    private String shareableToken;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private PersonalizedAssessmentStatus status;
}

