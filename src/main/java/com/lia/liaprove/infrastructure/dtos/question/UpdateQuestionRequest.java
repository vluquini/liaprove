package com.lia.liaprove.infrastructure.dtos.question;

import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.core.domain.question.KnowledgeArea;

import java.util.List;
import java.util.Set;

/**
 * DTO para a requisição de atualização de uma questão.
 * Campos nulos indicam que o valor não deve ser atualizado.
 */
public record UpdateQuestionRequest(
        String title,
        String description,
        Set<KnowledgeArea> knowledgeAreas,
        List<Alternative> alternatives
) {}
