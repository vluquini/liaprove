package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.exceptions.AuthorizationException;
import com.lia.liaprove.core.exceptions.UserNotFoundException;

import java.util.UUID;

/**
 * Caso de uso para criar uma nova avaliação (Assessment).
 * Abrange tanto a criação de avaliações personalizadas por recrutadores
 * quanto a geração de avaliações pelo sistema.
 */
public interface CreateAssessmentUseCase {

    // TODO: Definir um DTO para a criação de Assessment

    /**
     * Executa a criação de uma nova avaliação.
     *
     * @param assessment O objeto de avaliação a ser criado.
     * @param creatorId O ID do usuário que está criando a avaliação (relevante para permissões).
     * @return A avaliação recém-criada.
     * @throws AuthorizationException se o usuário não tiver permissão para criar a avaliação.
     * @throws UserNotFoundException se o ID do criador não corresponder a um usuário válido.
     */
    Assessment execute(Assessment assessment, UUID creatorId);
}