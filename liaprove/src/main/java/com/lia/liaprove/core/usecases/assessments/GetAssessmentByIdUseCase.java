package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.exceptions.AssessmentNotFoundException;

import java.util.UUID;

/**
 * Caso de uso para buscar os detalhes de uma avaliação específica pelo seu ID.
 */
public interface GetAssessmentByIdUseCase {

    /**
     * Busca e retorna uma avaliação com base no ID fornecido.
     *
     * @param id O ID da avaliação a ser buscada.
     * @return O objeto Assessment correspondente.
     * @throws AssessmentNotFoundException se nenhuma avaliação for encontrada com o ID fornecido.
     */
    Assessment execute(UUID id);
}
