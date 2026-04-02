package com.lia.liaprove.core.domain.question;

import java.util.Set;

/**
 * Interface comum para representações de conteúdo de uma questão.
 * Define os campos básicos necessários para validação e criação.
 */
public interface QuestionContent {
    String title();
    String description();
    Set<KnowledgeArea> knowledgeAreas();
    DifficultyLevel difficultyByCommunity();
    RelevanceLevel relevanceByCommunity();
}
