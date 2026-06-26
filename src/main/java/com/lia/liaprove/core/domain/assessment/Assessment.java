package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.question.Question;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
    // Limite de tempo da avaliação
    private Duration evaluationTimer;

    public Assessment(UUID id, String title, String description, LocalDateTime creationDate, List<Question> questions,
                      Duration evaluationTimer) {
        this.id = id;
        setTitle(title);
        setDescription(description);
        setCreationDate(creationDate);
        setQuestions(questions);
        setEvaluationTimer(evaluationTimer);
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
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = Objects.requireNonNull(creationDate, "creationDate must not be null");
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = Objects.requireNonNull(questions, "questions must not be null");
    }

    public Duration getEvaluationTimer() {
        return evaluationTimer;
    }

    public void setEvaluationTimer(Duration evaluationTimer) {
        this.evaluationTimer = Objects.requireNonNull(evaluationTimer, "evaluationTimer must not be null");
    }
}
