package com.lia.liaprove.application.services.assessment.dto;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.QuestionType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * DTO que carrega os critérios de filtragem para sugestão de questões.
 */
public class SuggestionCriteriaDto {
    private final Set<KnowledgeArea> knowledgeAreas;
    private final Set<DifficultyLevel> difficultyLevels;
    private final Set<QuestionType> questionTypes;
    private final int page;
    private final int pageSize;
    private final List<UUID> excludeIds;

    public SuggestionCriteriaDto(Set<KnowledgeArea> knowledgeAreas, Set<DifficultyLevel> difficultyLevels,
                                 Integer page, Integer pageSize, List<UUID> excludeIds) {
        this(knowledgeAreas, difficultyLevels, null, page, pageSize, excludeIds);
    }

    public SuggestionCriteriaDto(Set<KnowledgeArea> knowledgeAreas, Set<DifficultyLevel> difficultyLevels,
                                 Set<QuestionType> questionTypes, Integer page, Integer pageSize, List<UUID> excludeIds) {
        this.knowledgeAreas = knowledgeAreas;
        this.difficultyLevels = difficultyLevels;
        this.questionTypes = questionTypes;
        this.page = (page == null || page < 1) ? 1 : page;
        this.pageSize = (pageSize == null || pageSize <= 0) ? 10 : pageSize;
        this.excludeIds = (excludeIds == null) ? Collections.emptyList() : excludeIds;
    }

    public Optional<Set<KnowledgeArea>> getKnowledgeAreas() {
        return (knowledgeAreas == null || knowledgeAreas.isEmpty()) ? Optional.empty() : Optional.of(knowledgeAreas);
    }

    public Optional<Set<DifficultyLevel>> getDifficultyLevels() {
        return (difficultyLevels == null || difficultyLevels.isEmpty()) ? Optional.empty() : Optional.of(difficultyLevels);
    }

    public Optional<Set<QuestionType>> getQuestionTypes() {
        return (questionTypes == null || questionTypes.isEmpty()) ? Optional.empty() : Optional.of(questionTypes);
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public List<UUID> getExcludeIds() {
        return excludeIds;
    }
}
