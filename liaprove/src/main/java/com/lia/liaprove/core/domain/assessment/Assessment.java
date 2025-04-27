package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.metrics.Feedback;
import com.lia.liaprove.core.domain.question.Question;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Assessment {
    private String title;
    private String description;
    // Como os Recruiters podem criar novas avaliações, a data de criação é uma informação importante
    private LocalDateTime creationDate;
    private List<Question> questions;
    private List<Feedback> feedbacks;
    // Limite de tempo da avaliação
    private Duration evaluationTimer;
    // Data de expiração da avaliação (se necessário)
    private LocalDateTime expirationDate;
    // Total de vezes que a avaliação foi realizada
    private int totalAttempts;
    private AssessmentStatus status;
    // Indica se é possível refazer a avaliação (útil para avaliações personalizadas)
    private Boolean allowsRetake;
    // Taxa de acerto da avaliação
    private Integer accuracyRate;

}
