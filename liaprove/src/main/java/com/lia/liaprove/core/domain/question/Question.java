package com.lia.liaprove.core.domain.question;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class Question {
    private String title;
    private String description;
    private DifficultyLevel difficultyLevel;
    // A comunidade avaliará o nível de dificuldade da questão. Este atributo representa quantos votos cada nível recebeu.
    private EnumMap<DifficultyLevel, Integer> difficultyLevelVotes;
    // A comunidade avaliará o nível de conhecimento da questão. Este atributo representa quantos votos cada nível recebeu.
    private Map<String, Integer> knowledgeAreaVotes = new HashMap<>();
    // A comunidade avaliará a relevância da questão. Este atributo representa quantos votos cada nível recebeu.
    private int[] relevanceVotes = new int[5]; // Relevância de 1 a 5
    private LocalDateTime submissionDate;
    // Status da questão na plataforma
    private QuestionStatus status;
    // Número de votos totais que a questão recebeu
    private Integer totalVotes;
    private Boolean preEvaluatedByLLM;
    // Nível de relevância, atribuído pela LLM
    private byte relevanceByLLM;
    private Boolean isAproved;
    private int upVote;
    private int downVote;
    // Número de vezes que o recruiter usou essa questão. Será utilizada no cálculo de sugestão pelas RBs.
    private int recruiterUsageCount;

}
