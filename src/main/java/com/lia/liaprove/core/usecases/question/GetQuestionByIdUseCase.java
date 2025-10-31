package com.lia.liaprove.core.usecases.question;

import com.lia.liaprove.core.domain.question.Question;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface para o caso de uso de recuperação de uma questão pelo seu ID.
 */
public interface GetQuestionByIdUseCase {

    /**
     * Executa a recuperação de uma questão pelo seu identificador único.
     *
     * @param questionId O ID da questão a ser recuperada.
     * @return Um Optional contendo a questão, se encontrada, ou um Optional vazio caso contrário.
     */
    Optional<Question> execute(UUID questionId);
}
