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
