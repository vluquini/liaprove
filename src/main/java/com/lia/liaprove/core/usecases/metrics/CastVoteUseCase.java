package com.lia.liaprove.core.usecases.metrics;

import com.lia.liaprove.core.domain.metrics.VoteType;
import com.lia.liaprove.core.exceptions.QuestionNotFoundException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;

import java.util.UUID;

/**
 * Use case para permitir que um usuário vote em uma questão.
 * Este voto é formal e contribui para o processo de curadoria da questão.
 */
public interface CastVoteUseCase {
    /**
     * Registra o voto de um usuário em uma questão específica.
     *
     * @param userId O ID do usuário que está votando.
     * @param questionId O ID da questão que está sendo votada.
     * @param voteType O tipo de voto (APPROVE ou REJECT).
     * @throws UserNotFoundException se o usuário não for encontrado.
     * @throws QuestionNotFoundException se a questão não for encontrada.
     */
    void castVote(UUID userId, UUID questionId, VoteType voteType);
}
