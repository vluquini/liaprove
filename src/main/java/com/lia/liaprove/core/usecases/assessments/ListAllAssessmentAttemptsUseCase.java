package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso administrativo para listar todas as tentativas de avaliação no sistema.
 */
public interface ListAllAssessmentAttemptsUseCase {

    /**
     * Retorna uma lista de todas as tentativas de avaliação, de todos os tipos e usuários.
     * O acesso é restrito a usuários com a role de ADMIN.
     *
     * @param requesterId O ID do usuário que solicita a lista (deve ser um Admin).
     * @return Uma lista de objetos {@link AssessmentAttempt}.
     * @throws AuthorizationException se o solicitante não for um Admin.
     */
    List<AssessmentAttempt> execute(UUID requesterId);
}
