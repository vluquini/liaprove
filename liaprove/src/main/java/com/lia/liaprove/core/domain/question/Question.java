package com.lia.liaprove.core.domain.question;

import java.time.LocalDateTime;
import java.util.*;

public class Question {
    private String title;
    private String description;
    private DifficultyLevel difficultyLevel;
    // A comunidade avaliará o nível de dificuldade da questão. Este atributo representa quantos votos cada nível recebeu.
    private EnumMap<DifficultyLevel, Integer> difficultyLevelVotes;
    // A comunidade avaliará a área de conhecimento da questão. Este atributo representa quantos votos cada área recebeu.
    private List<KnowledgeArea> knowledgeAreas = new ArrayList<>();
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
