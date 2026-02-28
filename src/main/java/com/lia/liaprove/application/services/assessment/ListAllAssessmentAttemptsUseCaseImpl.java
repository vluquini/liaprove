package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.application.services.assessment.dto.ListAttemptsFilter;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import com.lia.liaprove.core.usecases.assessments.ListAllAssessmentAttemptsUseCase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Implementação do caso de uso administrativo para listar todas as tentativas de avaliação.
 */
public class ListAllAssessmentAttemptsUseCaseImpl implements ListAllAssessmentAttemptsUseCase {

    private final UserGateway userGateway;
    private final AssessmentAttemptGateway attemptGateway;

    public ListAllAssessmentAttemptsUseCaseImpl(UserGateway userGateway, AssessmentAttemptGateway attemptGateway) {
        this.userGateway = userGateway;
        this.attemptGateway = attemptGateway;
    }

    @Override
    public List<AssessmentAttempt> execute(UUID requesterId, Optional<Boolean> isPersonalized,
                                           Optional<LocalDateTime> startDate, Optional<LocalDateTime> endDate,
                                           Optional<Set<AssessmentAttemptStatus>> statuses) {
        // 1. Autorização
        User requester = userGateway.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("Requester with ID " + requesterId + " not found."));

        if (requester.getRole() != UserRole.ADMIN) {
            throw new AuthorizationException("Only ADMIN users can list all assessment attempts.");
        }

        // 2. Montar o filtro e delegar ao gateway
        ListAttemptsFilter filter = new ListAttemptsFilter(isPersonalized, startDate, endDate, statuses);
        
        return attemptGateway.findAllByCriteria(filter);
    }
}
