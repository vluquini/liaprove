package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.exceptions.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.AuthorizationException;

import java.util.UUID;

/**
 * Caso de uso para buscar uma tentativa de avaliação específica pelo seu ID.
 */
public interface GetAssessmentAttemptUseCase {

    /**
     * Busca e retorna uma tentativa de avaliação com base no ID fornecido, verificando as permissões do usuário.
     *
     * @param id O ID da tentativa de avaliação a ser buscada.
     * @param userId O ID do usuário que está solicitando a informação.
     * @return O objeto AssessmentAttempt correspondente.
     * @throws AssessmentNotFoundException se nenhuma tentativa de avaliação for encontrada com o ID fornecido.
     * @throws AuthorizationException se o usuário solicitante não for o dono da tentativa de avaliação.
     */
    AssessmentAttempt execute(UUID id, UUID userId);
}
