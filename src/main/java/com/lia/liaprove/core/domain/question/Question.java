package com.lia.liaprove.core.domain.question;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Classe abstrata base para todas as questões do sistema, contendo atributos comuns e informações de autoria.
 */
public abstract class Question {
    private UUID id;
    private UUID authorId;
    private String title;
    private String description;
    private Set<KnowledgeArea> knowledgeAreas = new HashSet<>();
    private DifficultyLevel difficultyByCommunity;
    private RelevanceLevel relevanceByCommunity;
    private LocalDateTime submissionDate;
    private LocalDateTime votingEndDate;
    private QuestionStatus status;
    private RelevanceLevel relevanceByLLM;
    // Número de vezes que o recruiter usou essa questão. É utilizada no cálculo de sugestão pelas RBs.
    private int recruiterUsageCount;

    public Question(){}

    public Question(UUID id, UUID authorId, String title, String description, Set<KnowledgeArea> knowledgeAreas,
                    DifficultyLevel difficultyByCommunity, RelevanceLevel relevanceByCommunity, LocalDateTime submissionDate,
                    LocalDateTime votingEndDate, QuestionStatus status, RelevanceLevel relevanceByLLM, int recruiterUsageCount) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.description = description;
        this.knowledgeAreas = knowledgeAreas;
        this.difficultyByCommunity = difficultyByCommunity;
        this.relevanceByCommunity = relevanceByCommunity;
        this.submissionDate = submissionDate;
        this.votingEndDate = votingEndDate;
        this.status = status;
        this.relevanceByLLM = relevanceByLLM;
        this.recruiterUsageCount = recruiterUsageCount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<KnowledgeArea> getKnowledgeAreas() {
        return knowledgeAreas;
    }

    public void setKnowledgeAreas(Set<KnowledgeArea> knowledgeAreas) {
        this.knowledgeAreas = knowledgeAreas;
    }

    public DifficultyLevel getDifficultyByCommunity() {
        return difficultyByCommunity;
    }

    public void setDifficultyByCommunity(DifficultyLevel difficultyByCommunity) {
        this.difficultyByCommunity = difficultyByCommunity;
    }

    public RelevanceLevel getRelevanceByCommunity() {
        return relevanceByCommunity;
    }

    public void setRelevanceByCommunity(RelevanceLevel relevanceByCommunity) {
        this.relevanceByCommunity = relevanceByCommunity;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public LocalDateTime getVotingEndDate() {
        return votingEndDate;
    }

    public void setVotingEndDate(LocalDateTime votingEndDate) {
        this.votingEndDate = votingEndDate;
    }

    public QuestionStatus getStatus() {
        return status;
    }

    public void setStatus(QuestionStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("The new status cannot be null.");
        }
        if (this.status == newStatus) {
            return;
        }
        this.status = newStatus;
    }

    public RelevanceLevel getRelevanceByLLM() {
        return relevanceByLLM;
    }

    public void setRelevanceByLLM(RelevanceLevel relevanceByLLM) {
        this.relevanceByLLM = relevanceByLLM;
    }

    public int getRecruiterUsageCount() {
        return recruiterUsageCount;
    }

    public void setRecruiterUsageCount(int recruiterUsageCount) {
        this.recruiterUsageCount = recruiterUsageCount;
    }

}
