package com.lia.liaprove.core.usecases.question;

import com.lia.liaprove.core.domain.question.Question;

import java.util.UUID;

/**
 * Caso de uso para avaliar o resultado da votação de uma questão.
 * A implementação deste caso de uso é responsável por processar o resultado
 * do período de votação e atualizar o status da questão (ex: para APPROVED ou REJECTED).
 */
public interface EvaluateVotingResultUseCase {
    Question evaluate(UUID questionId);
}
