package com.lia.liaprove.core.domain.Question;

import java.time.LocalDateTime;

public class Question {
    private String title;
    private String description;
    private DifficultyLevel difficultyLevel;
    private LocalDateTime submissionDate;
    // Talvez seja melhor um ENUM aqui
    private String status;
    private Integer votes;
    private String preEvaluatedByLLM;
    private Float relevanceScore;
    private Boolean isAproved;
    private int upVote;
    private int downVote;

}
