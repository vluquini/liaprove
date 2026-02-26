package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;

import java.util.UUID;

/**
 * Caso de uso para buscar os detalhes completos de uma tentativa de avaliação.
 * <p>
 * O uso é restrito ao Recrutador que criou a avaliação ou a um usuário com role ADMIN.
 */
public interface GetAssessmentAttemptDetailsUseCase {

    /**
     * Busca e retorna os detalhes de uma tentativa, validando a permissão do solicitante.
     *
     * @param attemptId   O ID da tentativa de avaliação a ser buscada.
     * @param requesterId O ID do usuário (Recrutador ou Admin) que está solicitando a informação.
     * @return O objeto AssessmentAttempt completo com questões e respostas.
     * @throws AssessmentNotFoundException se nenhuma tentativa for encontrada com o ID fornecido.
     * @throws AuthorizationException      se o solicitante não for o criador da avaliação ou um Admin.
     */
    AssessmentAttempt execute(UUID attemptId, UUID requesterId);
}
