package com.lia.liaprove.application.gateways.metrics;

import com.lia.liaprove.core.domain.metrics.Vote;

import java.util.List;
import java.util.UUID;

public interface VoteGateway {
    /**
     * Salva um voto no sistema.
     * @param vote O objeto Vote a ser salvo.
     */
    void save(Vote vote);

    /**
     * Lista votos de uma Question.
     * @param questionId O objeto Vote a ser salvo.
     */
    List<Vote> findByQuestionId(UUID questionId);
}
