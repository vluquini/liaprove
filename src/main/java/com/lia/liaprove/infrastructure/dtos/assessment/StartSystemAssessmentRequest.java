package com.lia.liaprove.infrastructure.dtos.assessment;

import com.lia.liaprove.application.services.assessment.dto.SystemAssessmentType;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import jakarta.validation.constraints.NotNull;


public record StartSystemAssessmentRequest(
    @NotNull(message = "Knowledge area is required")
    KnowledgeArea knowledgeArea,

    @NotNull(message = "Difficulty level is required")
    DifficultyLevel difficultyLevel,
    SystemAssessmentType type // Opcional, default MULTIPLE_CHOICE no use case
) {}
