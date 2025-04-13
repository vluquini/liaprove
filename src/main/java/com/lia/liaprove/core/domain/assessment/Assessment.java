package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.relationship.AssessmentQuestion;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Assessment {
    private String title;
    private String description;
    // Como os Recruiters podem criar novas avaliações, a data de criação é uma informação importante
    private LocalDateTime creationDate;
    private List<AssessmentQuestion> questions;
    // Limite de tempo da avaliação
    private Duration evaluationTimer;
    // Data de expiração da avaliação (se necessário)
    private LocalDateTime expirationDate;
    // Total de vezes que a avaliação foi realizada
    private int totalAttempts;
    private AssessmentStatus status;
    // Indica se é possível refazer a avaliação
    private Boolean allowsRetake;

}
