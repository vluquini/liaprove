package com.lia.liaprove.application.services.assessment.dto;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.Question;

import java.util.Set;
import java.util.UUID;

/**
 * DTO que representa uma questão sugerida com seu score de relevância.
 */
public record ScoredQuestionDto(
        UUID id,
        String title,
        String description,
        Set<KnowledgeArea> knowledgeAreas,
        DifficultyLevel difficultyLevel,
        double score
) {
    public static ScoredQuestionDto fromDomain(Question question, double score) {
        return new ScoredQuestionDto(
                question.getId(),
                question.getTitle(),
                question.getDescription(),
                question.getKnowledgeAreas(),
                question.getDifficultyByCommunity(),
                score
        );
    }
}
