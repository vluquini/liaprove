package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;

import java.util.Optional;
import java.util.Set;

/**
 * Representa os critérios para filtrar sugestões de questões.
 */
public class SuggestionCriteria {
    private final Set<KnowledgeArea> knowledgeAreas;
    private final Set<DifficultyLevel> difficultyLevels;
    private final int limit;

    public SuggestionCriteria(Set<KnowledgeArea> knowledgeAreas, Set<DifficultyLevel> difficultyLevels, int limit) {
        this.knowledgeAreas = knowledgeAreas;
        this.difficultyLevels = difficultyLevels;
        this.limit = (limit <= 0) ? 10 : limit;
    }

    public Optional<Set<KnowledgeArea>> getKnowledgeAreas() {
        return (knowledgeAreas == null || knowledgeAreas.isEmpty()) ? Optional.empty() : Optional.of(knowledgeAreas);
    }

    public Optional<Set<DifficultyLevel>> getDifficultyLevels() {
        return (difficultyLevels == null || difficultyLevels.isEmpty()) ? Optional.empty() : Optional.of(difficultyLevels);
    }

    public int getLimit() {
        return limit;
    }
}
