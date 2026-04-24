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
     * Busca o voto de um usuário para uma questão específica.
     * @param userId identificador do usuário.
     * @param questionId identificador da questão.
     * @return voto encontrado, quando existir.
     */
    List<Vote> findByUserIdAndQuestionId(UUID userId, UUID questionId);

    /**
     * Remove um voto do sistema.
     * @param vote voto a ser removido.
     */
    void delete(Vote vote);

    /**
     * Lista votos de uma Question.
     * @param questionId O objeto Vote a ser salvo.
     */
    List<Vote> findVotesByQuestionId(UUID questionId);
}
