package com.lia.liaprove.infrastructure.entities.metrics;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@DiscriminatorValue("ASSESSMENT")
@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackAssessmentEntity extends FeedbackEntity {
    // Placeholder for the actual relation to an AssessmentEntity
    @Column(name = "assessment_id")
    private UUID assessmentId;
}
