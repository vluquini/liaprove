package com.lia.liaprove.core.usecases.metrics;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.exceptions.QuestionNotFoundException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;

import java.util.UUID;

/**
 * Use case para permitir que um usuário envie um feedback detalhado (comentário)
 * sobre uma questão específica.
 */
public interface SubmitFeedbackOnQuestionUseCase {
    /**
     * Envia um feedback textual sobre uma questão, incluindo sugestões de
     * dificuldade, área de conhecimento e nível de relevância.
     *
     * @param userId O ID do usuário que está enviando o feedback.
     * @param questionId O ID da questão que está recebendo o feedback.
     * @param comment O texto do feedback.
     * @param difficultyLevel Sugestão de nível de dificuldade para a questão.
     * @param knowledgeArea Sugestão de área de conhecimento para a questão.
     * @param relevanceLevel Sugestão de nível de relevância para a questão.
     * @throws UserNotFoundException se o usuário não for encontrado.
     * @throws QuestionNotFoundException se a questão não for encontrada.
     */
    void submitFeedback(UUID userId, UUID questionId, String comment, DifficultyLevel difficultyLevel,
                        KnowledgeArea knowledgeArea, RelevanceLevel relevanceLevel);
}
