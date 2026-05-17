package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.Question;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Representa uma avaliação padrão gerada pelo sistema.
 * Estende a classe base Assessment e não adiciona propriedades específicas,
 * diferenciando-se apenas por sua origem ser o próprio sistema.
 */
public class SystemAssessment extends Assessment{
    private KnowledgeArea knowledgeArea;
    private DifficultyLevel difficultyLevel;

    public SystemAssessment(UUID id, String title, String description, LocalDateTime creationDate,
                            List<Question> questions, Duration evaluationTimer) {
        super(id, title, description, creationDate, questions, evaluationTimer);
    }

    public SystemAssessment(UUID id, String title, String description, LocalDateTime creationDate,
                            List<Question> questions, Duration evaluationTimer,
                            KnowledgeArea knowledgeArea, DifficultyLevel difficultyLevel) {
        super(id, title, description, creationDate, questions, evaluationTimer);
        this.knowledgeArea = knowledgeArea;
        this.difficultyLevel = difficultyLevel;
    }

    public KnowledgeArea getKnowledgeArea() {
        return knowledgeArea;
    }

    public void setKnowledgeArea(KnowledgeArea knowledgeArea) {
        this.knowledgeArea = knowledgeArea;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
}
