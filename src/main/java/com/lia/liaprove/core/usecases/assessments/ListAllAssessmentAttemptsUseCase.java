package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Caso de uso administrativo para listar todas as tentativas de avaliação no sistema.
 */
public interface ListAllAssessmentAttemptsUseCase {

    /**
     * Retorna uma lista de todas as tentativas de avaliação, com base nos filtros fornecidos.
     * O acesso é restrito a usuários com a role de ADMIN.
     *
     * @param requesterId    O ID do usuário que solicita a lista (deve ser um Admin).
     * @param isPersonalized Filtra por tipo de avaliação (opcional).
     * @param startDate      Filtra tentativas iniciadas após esta data (opcional).
     * @param endDate        Filtra tentativas iniciadas antes desta data (opcional).
     * @param statuses       Filtra por um conjunto de status (opcional).
     * @return Uma lista de objetos {@link AssessmentAttempt}.
     * @throws AuthorizationException se o solicitante não for um Admin.
     */
    List<AssessmentAttempt> execute(UUID requesterId, Optional<Boolean> isPersonalized, Optional<LocalDateTime> startDate,
                                    Optional<LocalDateTime> endDate, Optional<Set<AssessmentAttemptStatus>> statuses);
}
