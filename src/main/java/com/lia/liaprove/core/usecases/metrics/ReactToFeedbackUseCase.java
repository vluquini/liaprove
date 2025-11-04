package com.lia.liaprove.core.usecases.metrics;

import com.lia.liaprove.core.domain.metrics.ReactionType;
import com.lia.liaprove.core.exceptions.FeedbackNotFoundException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;

import java.util.UUID;

/**
 * Use case para permitir que um usuário reaja (like/dislike) a um feedback (comentário) existente.
 */
public interface ReactToFeedbackUseCase {
    /**
     * Registra ou atualiza a reação de um usuário a um feedback específico.
     *
     * @param userId O ID do usuário que está reagindo.
     * @param feedbackId O ID do feedback que está recebendo a reação.
     * @param reactionType O tipo de reação (LIKE ou DISLIKE).
     * @throws UserNotFoundException se o usuário não for encontrado.
     * @throws FeedbackNotFoundException se o feedback não for encontrado.
     */
    void reactToFeedback(UUID userId, UUID feedbackId, ReactionType reactionType);
}
