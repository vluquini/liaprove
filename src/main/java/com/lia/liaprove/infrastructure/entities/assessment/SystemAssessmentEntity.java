package com.lia.liaprove.infrastructure.entities.assessment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("SYSTEM")
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemAssessmentEntity extends AssessmentEntity {
}

