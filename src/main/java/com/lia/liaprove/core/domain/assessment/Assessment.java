package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.metrics.FeedbackAssessment;
import com.lia.liaprove.core.domain.question.Question;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Representa a base abstrata para todos os tipos de avaliações no sistema.
 * Define as propriedades e comportamentos comuns a avaliações como testes de múltipla escolha
 * e mini-projetos, sejam eles gerados pelo sistema ou personalizados por recrutadores.
 */
public abstract class Assessment {
    private UUID id;
    private String title;
    private String description;
    // Como os Recruiters podem criar novas avaliações, a data de criação é uma informação importante
    private LocalDateTime creationDate;
    private List<Question> questions;
    private List<FeedbackAssessment> feedbacks;
    // Limite de tempo da avaliação
    private Duration evaluationTimer;

    public Assessment(UUID id, String title, String description, LocalDateTime creationDate, List<Question> questions,
                      List<FeedbackAssessment> feedbacks, Duration evaluationTimer) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
        this.questions = questions;
        this.feedbacks = feedbacks;
        this.evaluationTimer = evaluationTimer;
    }

    public UUID getId() {
        return id;
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<FeedbackAssessment> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<FeedbackAssessment> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public Duration getEvaluationTimer() {
        return evaluationTimer;
    }

    public void setEvaluationTimer(Duration evaluationTimer) {
        this.evaluationTimer = evaluationTimer;
    }
}
