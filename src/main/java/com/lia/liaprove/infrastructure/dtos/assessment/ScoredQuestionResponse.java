package com.lia.liaprove.infrastructure.dtos.assessment;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;

import java.util.Set;
import java.util.UUID;

public record ScoredQuestionResponse(
    UUID id,
    String title,
    String description,
    Set<KnowledgeArea> knowledgeAreas,
    DifficultyLevel difficultyLevel,
    double score
) {}
