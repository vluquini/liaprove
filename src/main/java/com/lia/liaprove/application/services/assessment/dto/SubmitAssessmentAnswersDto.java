package com.lia.liaprove.application.services.assessment.dto;

import java.util.List;
import java.util.UUID;

/**
 * DTO para receber as respostas de uma tentativa de avaliação.
 */
public record SubmitAssessmentAnswersDto(
        UUID attemptId,
        List<QuestionAnswerDto> answers
) {
    /**
     * DTO interno para representar a resposta de uma questão específica.
     * Pode conter o ID da alternativa selecionada (para múltipla escolha)
     * ou o link do projeto (para mini-projetos).
     */
    public record QuestionAnswerDto(
            UUID questionId,
            UUID selectedAlternativeId,
            String projectUrl
    ) {}
}
