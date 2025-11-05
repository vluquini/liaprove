package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.Assessment;

import java.util.List;
import java.util.Map;

/**
 * Caso de uso para listar as avaliações disponíveis.
 */
public interface ListAssessmentsUseCase {

    // TODO: Definir um DTO para os filtros

    /**
     * Retorna uma lista de avaliações, potencialmente filtrada por critérios específicos.
     *
     * @param filters Um mapa de filtros a serem aplicados (ex: "knowledgeArea", "difficultyLevel"). Pode ser nulo ou vazio.
     * @return Uma lista de objetos Assessment que correspondem aos filtros.
     */
    List<Assessment> execute(Map<String, Object> filters);
}
