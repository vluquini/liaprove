package com.lia.liaprove.core.domain.assessment;

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
    public SystemAssessment(UUID id, String title, String description, LocalDateTime creationDate,
                            List<Question> questions, Duration evaluationTimer) {
        super(id, title, description, creationDate, questions, evaluationTimer);
    }

}
