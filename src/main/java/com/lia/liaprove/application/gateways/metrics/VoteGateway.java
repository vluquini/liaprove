package com.lia.liaprove.application.gateways.metrics;

import com.lia.liaprove.core.domain.metrics.QuestionVote;

import java.util.List;
import java.util.UUID;

public interface VoteGateway {
    /**
     * Salva um voto no sistema.
     * @param questionVote O objeto Vote a ser salvo.
     */
    void save(QuestionVote questionVote);

    /**
     * Busca o voto de um usuário para uma questão específica.
     * @param userId identificador do usuário.
     * @param questionId identificador da questão.
     * @return voto encontrado, quando existir.
     */
    List<QuestionVote> findByUserIdAndQuestionId(UUID userId, UUID questionId);

    /**
     * Remove um voto do sistema.
     * @param questionVote voto a ser removido.
     */
    void delete(QuestionVote questionVote);

    /**
     * Lista votos de uma Question.
     * @param questionId O objeto Vote a ser salvo.
     */
    List<QuestionVote> findVotesByQuestionId(UUID questionId);
}
