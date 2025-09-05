package com.lia.liaprove.core.domain.question;

import java.time.LocalDateTime;
import java.util.*;

public class Question {
    private UUID id;
    private String title;
    private String description;
    private Set<KnowledgeArea> knowledgeAreas = new HashSet<>();
    // Nível de dificuldade da Questão atribuído pela comunidade
    private DifficultyLevel difficultyLevel;
    // Nível de relevância da Questão atribuído pela comunidade
    private RelevanceLevel relevanceByCommunity;
    private LocalDateTime submissionDate;
    // Status da questão na plataforma
    private QuestionStatus status;
    // Nível de relevância, atribuído pela LLM - Este campo poderá ser utilizado pelas Redes Bayesianas ao sugerir questões
    // aos Recruiters
    private RelevanceLevel relevanceByLLM;
    // Número de vezes que o recruiter usou essa questão. Será utilizada no cálculo de sugestão pelas RBs.
    private int recruiterUsageCount;

    public Question(){}

    public Question(UUID id, String title, String description, Set<KnowledgeArea> knowledgeAreas, DifficultyLevel difficultyLevel,
                    RelevanceLevel relevanceByCommunity, LocalDateTime submissionDate, QuestionStatus status, RelevanceLevel relevanceByLLM, int recruiterUsageCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.knowledgeAreas = knowledgeAreas;
        this.difficultyLevel = difficultyLevel;
        this.relevanceByCommunity = relevanceByCommunity;
        this.submissionDate = submissionDate;
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

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
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

    public QuestionStatus getStatus() {
        return status;
    }

    public void setStatus(QuestionStatus status) {
        this.status = status;
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


    //    public void addKnowledgeArea(KnowledgeArea area) {
//        knowledgeAreas.add(area);
//    }
//
//    // Permitir votação em uma área de conhecimento específica
//    public void voteKnowledgeArea(String areaName) {
//        for (KnowledgeArea area : knowledgeAreas) {
//            if (area.getName().equalsIgnoreCase(areaName)) {
//                area.vote(); // Incrementa o contador de votos na área
//                return;
//            }
//        }
//        throw new IllegalArgumentException("Área de conhecimento não encontrada: " + areaName);
//    }
//
//    // Exibir os votos para todas as áreas de conhecimento
//    public void displayKnowledgeAreaVotes() {
//        for (KnowledgeArea area : knowledgeAreas) {
//            System.out.println(area);
//        }
//    }

}
