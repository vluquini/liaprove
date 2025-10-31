package com.lia.liaprove.core.usecases.question;

import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface para o caso de uso de moderação de uma questão.
 * Permite que administradores alterem o status de uma questão.
 */
public interface ModerateQuestionUseCase {

    /**
     * Executa a moderação de uma questão, alterando seu status.
     *
     * @param questionId O ID da questão a ser moderada.
     * @param command Objeto de comando contendo o novo status e, opcionalmente, o motivo da moderação.
     * @return A questão moderada, se encontrada, ou um Optional vazio caso contrário.
     */
    Optional<Question> execute(UUID questionId, ModerateQuestionCommand command);

    /**
     * Objeto de comando (DTO) para a moderação de uma questão.
     */
    record ModerateQuestionCommand(
            QuestionStatus newStatus,
            Optional<String> moderationReason
    ) {}

}
