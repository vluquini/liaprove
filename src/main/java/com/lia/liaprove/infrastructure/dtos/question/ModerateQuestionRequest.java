package com.lia.liaprove.infrastructure.dtos.question;

import com.lia.liaprove.core.domain.question.QuestionStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para a requisição de moderação de uma questão.
 */
public record ModerateQuestionRequest(
        @NotNull(message = "The new status of the question cannot be null.")
        QuestionStatus newStatus
) { }
