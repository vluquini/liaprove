package com.lia.liaprove.core.usecases.question;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionContent;
import com.lia.liaprove.core.domain.question.RelevanceLevel;

import java.util.Set;
import java.util.UUID;

/**
 * Caso de uso específico para criação de perguntas abertas por recrutadores.
 */
public interface CreateRecruiterOpenQuestionUseCase {

    Question create(UUID authorId, OpenQuestionCommand command);

    record OpenQuestionCommand(
            String title,
            String description,
            Set<KnowledgeArea> knowledgeAreas,
            DifficultyLevel difficultyByCommunity,
            RelevanceLevel relevanceByCommunity,
            String guideline,
            OpenQuestionVisibility visibility
    ) implements QuestionContent {
    }
}
