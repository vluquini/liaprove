package com.lia.liaprove.application.services.question;

import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.RelevanceLevel;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Record que possui os parâmetros obrigatórios para criação de uma Question.
 * Os demais parâmetros são definidos na implementação
 * da {@code QuestionFactory}.
 */
public record QuestionCreateDto(
        UUID authorId,
        String title,
        String description,
        Set<KnowledgeArea> knowledgeAreas,
        DifficultyLevel difficultyByCommunity,
        RelevanceLevel relevanceByCommunity,
        RelevanceLevel relevanceByLLM,
        List<Alternative> alternatives
) {}


