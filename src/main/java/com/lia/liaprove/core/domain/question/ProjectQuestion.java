package com.lia.liaprove.core.domain.question;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Representa uma questão de projeto (mini-projeto).
 */
public class ProjectQuestion extends Question {
    public ProjectQuestion(){}

    public ProjectQuestion(UUID id, UUID authorId, String title, String description, Set<KnowledgeArea> knowledgeAreas,
                           DifficultyLevel difficultyByCommunity, RelevanceLevel relevanceByCommunity,
                           LocalDateTime submissionDate, LocalDateTime votingEndDate, QuestionStatus status, RelevanceLevel relevanceByLLM,
                           int recruiterUsageCount) {
        super(id, authorId, title, description, knowledgeAreas, difficultyByCommunity, relevanceByCommunity,
              submissionDate, votingEndDate, status, relevanceByLLM, recruiterUsageCount);
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.PROJECT;
    }
}
