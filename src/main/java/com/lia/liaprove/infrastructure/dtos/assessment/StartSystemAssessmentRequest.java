package com.lia.liaprove.infrastructure.dtos.assessment;

import com.lia.liaprove.application.services.assessment.dto.SystemAssessmentType;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record StartSystemAssessmentRequest(
    @NotEmpty(message = "At least one knowledge area must be selected")
    Set<KnowledgeArea> knowledgeAreas,

    @NotNull(message = "Difficulty level is required")
    DifficultyLevel difficultyLevel,
    SystemAssessmentType type // Opcional, default MULTIPLE_CHOICE no use case
) {}
