package com.lia.liaprove.core.domain.question;

import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;

import java.time.LocalDateTime;
import java.util.*;

public class Question {
    private UUID id;
    private String title;
    private String description;
    private DifficultyLevel difficultyLevel;
    // A comunidade avaliará o nível de dificuldade da questão. Este atributo representa quantos votos cada nível recebeu.
    private EnumMap<DifficultyLevel, Integer> difficultyLevelVotes;
    // A comunidade avaliará a área de conhecimento da questão. Este atributo representa quantos votos cada área recebeu.
    private List<KnowledgeArea> knowledgeAreas = new ArrayList<>();
    private List<FeedbackQuestion> feedbacks = new ArrayList<>();
    // A comunidade avaliará a relevância da questão. Este atributo representa quantos votos cada nível recebeu.
    private int[] relevanceVotes = new int[5]; // De 1 a 5
    private LocalDateTime submissionDate;
    // Status da questão na plataforma
    private QuestionStatus status;
    // Número de votos totais que a questão recebeu
    private Integer totalVotes;
    private Boolean preEvaluatedByLLM;
    // Nível de relevância, atribuído pela LLM (1 a 5)
    private byte relevanceByLLM;
    private Boolean isAproved;
    private int upVote;
    private int downVote;
    // Número de vezes que o recruiter usou essa questão. Será utilizada no cálculo de sugestão pelas RBs.
    private int recruiterUsageCount;

    public Question(UUID id, String title, String description, DifficultyLevel difficultyLevel, EnumMap<DifficultyLevel, Integer> difficultyLevelVotes,
                    List<KnowledgeArea> knowledgeAreas, List<FeedbackQuestion> feedbacks, int[] relevanceVotes, LocalDateTime submissionDate,
                    QuestionStatus status, Integer totalVotes, Boolean preEvaluatedByLLM, byte relevanceByLLM, Boolean isAproved, int upVote, int downVote,
                    int recruiterUsageCount) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.difficultyLevel = difficultyLevel;
        this.difficultyLevelVotes = difficultyLevelVotes;
        this.knowledgeAreas = knowledgeAreas;
        this.feedbacks = feedbacks;
        this.relevanceVotes = relevanceVotes;
        this.submissionDate = submissionDate;
        this.status = status;
        this.totalVotes = totalVotes;
        this.preEvaluatedByLLM = preEvaluatedByLLM;
        this.relevanceByLLM = relevanceByLLM;
        this.isAproved = isAproved;
        this.upVote = upVote;
        this.downVote = downVote;
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

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public EnumMap<DifficultyLevel, Integer> getDifficultyLevelVotes() {
        return difficultyLevelVotes;
    }

    public void setDifficultyLevelVotes(EnumMap<DifficultyLevel, Integer> difficultyLevelVotes) {
        this.difficultyLevelVotes = difficultyLevelVotes;
    }

    public List<KnowledgeArea> getKnowledgeAreas() {
        return knowledgeAreas;
    }

    public void setKnowledgeAreas(List<KnowledgeArea> knowledgeAreas) {
        this.knowledgeAreas = knowledgeAreas;
    }

    public List<FeedbackQuestion> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<FeedbackQuestion> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public int[] getRelevanceVotes() {
        return relevanceVotes;
    }

    public void setRelevanceVotes(int[] relevanceVotes) {
        this.relevanceVotes = relevanceVotes;
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

    public Integer getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(Integer totalVotes) {
        this.totalVotes = totalVotes;
    }

    public Boolean getPreEvaluatedByLLM() {
        return preEvaluatedByLLM;
    }

    public void setPreEvaluatedByLLM(Boolean preEvaluatedByLLM) {
        this.preEvaluatedByLLM = preEvaluatedByLLM;
    }

    public byte getRelevanceByLLM() {
        return relevanceByLLM;
    }

    public void setRelevanceByLLM(byte relevanceByLLM) {
        this.relevanceByLLM = relevanceByLLM;
    }

    public Boolean getAproved() {
        return isAproved;
    }

    public void setAproved(Boolean aproved) {
        isAproved = aproved;
    }

    public int getUpVote() {
        return upVote;
    }

    public void setUpVote(int upVote) {
        this.upVote = upVote;
    }

    public int getDownVote() {
        return downVote;
    }

    public void setDownVote(int downVote) {
        this.downVote = downVote;
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
