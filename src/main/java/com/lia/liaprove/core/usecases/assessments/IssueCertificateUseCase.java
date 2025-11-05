package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.exceptions.AssessmentNotFoundException;

import java.util.UUID;

/**
 * Caso de uso para emitir um novo certificado para uma tentativa de avaliação bem-sucedida.
 */
public interface IssueCertificateUseCase {

    /**
     * Gera e associa um certificado a uma tentativa de avaliação que atendeu aos critérios de aprovação.
     *
     * @param assessmentAttemptId O ID da tentativa de avaliação a ser certificada.
     * @return O objeto Certificate recém-criado.
     * @throws AssessmentNotFoundException se a tentativa de avaliação não for encontrada.
     * @throws IllegalStateException se a tentativa de avaliação não atender aos critérios para certificação (ex: pontuação insuficiente).
     */
    Certificate execute(UUID assessmentAttemptId);
}