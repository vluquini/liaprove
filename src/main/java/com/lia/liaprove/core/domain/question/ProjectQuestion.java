package com.lia.liaprove.core.domain.question;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Representa uma questão de projeto (mini-projeto), estendendo a classe Question e incluindo um URL para o projeto.
 */
public class ProjectQuestion extends Question {
    // Link do projeto desenvolvido pelo Usuário
    private String projectUrl;

    public ProjectQuestion(UUID id, UUID authorId, String title, String description, Set<KnowledgeArea> knowledgeAreas,
                           DifficultyLevel difficultyByCommunity, RelevanceLevel relevanceByCommunity,
                           LocalDateTime submissionDate, QuestionStatus status, RelevanceLevel relevanceByLLM,
                           int recruiterUsageCount, String projectUrl) {
        super(id, authorId, title, description, knowledgeAreas, difficultyByCommunity, relevanceByCommunity,
              submissionDate, status, relevanceByLLM, recruiterUsageCount);
        this.projectUrl = projectUrl;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }
}
