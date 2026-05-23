package com.lia.liaprove.infrastructure.dtos.metrics;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.RelevanceLevel;

import java.util.Set;
import java.util.UUID;

public record PublicMiniProjectQuestionResponse(
        UUID id,
        String title,
        String description,
        Set<KnowledgeArea> knowledgeAreas,
        DifficultyLevel difficulty,
        RelevanceLevel relevance
) {
}
