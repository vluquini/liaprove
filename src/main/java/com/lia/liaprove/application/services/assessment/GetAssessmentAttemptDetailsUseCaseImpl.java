package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import com.lia.liaprove.core.usecases.assessments.GetAssessmentAttemptDetailsUseCase;

import java.util.UUID;

/**
 * Implementação do caso de uso para buscar os detalhes de uma tentativa de avaliação.
 */
public class GetAssessmentAttemptDetailsUseCaseImpl implements GetAssessmentAttemptDetailsUseCase {

    private final AssessmentAttemptGateway attemptGateway;
    private final UserGateway userGateway;

    public GetAssessmentAttemptDetailsUseCaseImpl(AssessmentAttemptGateway attemptGateway, UserGateway userGateway) {
        this.attemptGateway = attemptGateway;
        this.userGateway = userGateway;
    }

    @Override
    public AssessmentAttempt execute(UUID attemptId, UUID requesterId) {
        // 1. Buscar a tentativa
        AssessmentAttempt attempt = attemptGateway.findByIdWithCreator(attemptId)
                .orElseThrow(() -> new AssessmentNotFoundException("Evaluation attempt with ID " + attemptId + " not found."));

        // 2. Buscar o usuário solicitante para checar a role
        User requester = userGateway.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("Requesting user not found."));

        // 3. Validar propriedade ou role de Admin
        validateAuthorization(requester, attempt);
        
        // 4. Retornar os detalhes completos
        return attempt;
    }

    private void validateAuthorization(User requester, AssessmentAttempt attempt) {
        // Permite o acesso se o usuário for ADMIN
        if (requester.getRole() == UserRole.ADMIN) {
            return;
        }

        Assessment assessment = attempt.getAssessment();
        if (!(assessment instanceof PersonalizedAssessment personalizedAssessment)) {
            throw new AuthorizationException("Access denied. This attempt does not belong to a personalized assessment.");
        }

        if (personalizedAssessment.getCreatedBy() == null || !personalizedAssessment.getCreatedBy().getId().equals(requester.getId())) {
            throw new AuthorizationException("You do not have permission to view the details of this attempt.");
        }
    }
}
