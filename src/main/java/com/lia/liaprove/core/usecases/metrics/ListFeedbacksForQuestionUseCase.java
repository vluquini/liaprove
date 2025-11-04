package com.lia.liaprove.core.usecases.metrics;

import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;
import com.lia.liaprove.core.exceptions.QuestionNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Use case para listar todos os feedbacks (comentários) associados a uma questão específica.
 */
public interface ListFeedbacksForQuestionUseCase {
    /**
     * Retorna uma lista de todos os feedbacks de uma questão.
     *
     * @param questionId O ID da questão para a qual os feedbacks serão listados.
     * @return Uma lista de objetos FeedbackQuestion. Pode ser vazia se não houver feedbacks.
     * @throws QuestionNotFoundException se a questão não for encontrada.
     */
    List<FeedbackQuestion> getFeedbacksForQuestion(UUID questionId);
}
