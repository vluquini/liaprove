package com.lia.liaprove.infrastructure.entities.assessment;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@DiscriminatorValue("SYSTEM")
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemAssessmentEntity extends AssessmentEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "knowledge_area", length = 64)
    private KnowledgeArea knowledgeArea;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", length = 32)
    private DifficultyLevel difficultyLevel;
}
