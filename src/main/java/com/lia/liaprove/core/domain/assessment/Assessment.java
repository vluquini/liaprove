package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.metrics.Feedback;
import com.lia.liaprove.core.domain.question.Question;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public abstract class Assessment {
    private String title;
    private String description;
    // Como os Recruiters podem criar novas avaliações, a data de criação é uma informação importante
    private LocalDateTime creationDate;
    private List<Question> questions;
    private List<Feedback> feedbacks;
    // Limite de tempo da avaliação
    private Duration evaluationTimer;
}
