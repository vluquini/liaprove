package com.lia.liaprove.application.gateways.metrics;

import com.lia.liaprove.core.domain.metrics.FeedbackAssessment;
import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedbackGateway {
    /**
     * Salva um feedback de avaliação no sistema.
     * @param feedback O objeto FeedbackAssessment a ser salvo.
     */
    void saveAssessmentFeedback(FeedbackAssessment feedback);

    /**
     * Encontra e retorna uma lista de feedbacks de questão para um ID de questão específico.
     * @param questionId O ID da questão.
     * @return Uma lista de FeedbackQuestion. Pode ser vazia se nenhum feedback for encontrado.
     */
    List<FeedbackQuestion> findFeedbacksByQuestionId(UUID questionId);

    /**
     * Encontra um feedback de questão pelo seu ID.
     * @param feedbackId O ID do feedback da questão.
     * @return Um Optional contendo o FeedbackQuestion se encontrado, ou vazio caso contrário.
     */
    Optional<FeedbackQuestion> findFeedbackQuestionById(UUID feedbackId);

    /**
     * Salva um feedback de questão no sistema.
     * @param feedback O objeto FeedbackQuestion a ser salvo.
     */
    void saveFeedbackQuestion(FeedbackQuestion feedback);
}
