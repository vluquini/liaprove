package com.lia.liaprove.core.usecases.metrics;

import com.lia.liaprove.core.domain.metrics.Vote;
import java.util.List;
import java.util.UUID;

/**
 * Use case para listar todos os votos (APPROVE/REJECT) associados a uma questão específica.
 */
public interface ListVotesForQuestionUseCase {
    /**
     * Retorna uma lista de todos os votos de uma questão.
     *
     * @param questionId O ID da questão para a qual os votos serão listados.
     * @return Uma lista de objetos Vote. Pode ser vazia se não houver votos.
     */
    List<Vote> getVotesForQuestion(UUID questionId);
}
