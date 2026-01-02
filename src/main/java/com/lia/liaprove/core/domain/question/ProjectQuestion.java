package com.lia.liaprove.core.domain.question;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Representa uma questão de projeto (mini-projeto), estendendo a classe Question e incluindo um URL para o projeto.
 */
public class ProjectQuestion extends Question {
    /**
     * Link do projeto desenvolvido pelo Usuário.
     * Só será preenchido quando o usuário responder, por isto não é preenchido no construtor.
     */
    private String projectUrl;

    public ProjectQuestion(){}

    public ProjectQuestion(UUID id, UUID authorId, String title, String description, Set<KnowledgeArea> knowledgeAreas,
                           DifficultyLevel difficultyByCommunity, RelevanceLevel relevanceByCommunity,
                           LocalDateTime submissionDate, LocalDateTime votingEndDate, QuestionStatus status, RelevanceLevel relevanceByLLM,
                           int recruiterUsageCount) {
        super(id, authorId, title, description, knowledgeAreas, difficultyByCommunity, relevanceByCommunity,
              submissionDate, votingEndDate, status, relevanceByLLM, recruiterUsageCount);
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    /**
     * Submissão de um link do projeto para a questão.
     */
    public void assignProjectSubmission(String projectUrl) {
        this.projectUrl = projectUrl;
    }
}
