package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.exceptions.AssessmentNotFoundException;

import java.util.Map;
import java.util.UUID;

/**
 * Caso de uso para registrar o resultado final de uma tentativa de avaliação.
 */
public interface RecordAssessmentResultUseCase {

    // TODO: Definir um DTO para as respostas

    /**
     * Calcula a pontuação, atualiza o status e salva o resultado de uma tentativa de avaliação.
     *
     * @param assessmentAttemptId O ID da tentativa de avaliação a ser finalizada.
     * @param answers Um mapa contendo as respostas do usuário (ex: ID da questão, resposta fornecida).
     * @return A entidade AssessmentAttempt atualizada com o resultado.
     * @throws AssessmentNotFoundException se a tentativa de avaliação não for encontrada.
     * @throws IllegalStateException se a avaliação já estiver finalizada.
     */
    AssessmentAttempt execute(UUID assessmentAttemptId, Map<UUID, String> answers);
}