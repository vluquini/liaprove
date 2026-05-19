package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import com.lia.liaprove.core.usecases.assessments.ListAttemptsForMyAssessmentUseCase;

import java.util.List;
import java.util.UUID;

/**
 * Implementação do caso de uso para listar tentativas de uma avaliação para seu criador.
 */
public class ListAttemptsForMyAssessmentUseCaseImpl implements ListAttemptsForMyAssessmentUseCase {

    private final AssessmentGateway assessmentGateway;
    private final AssessmentAttemptGateway attemptGateway;
    private final UserGateway userGateway;

    public ListAttemptsForMyAssessmentUseCaseImpl(AssessmentGateway assessmentGateway,
                                                  AssessmentAttemptGateway attemptGateway,
                                                  UserGateway userGateway) {
        this.assessmentGateway = assessmentGateway;
        this.attemptGateway = attemptGateway;
        this.userGateway = userGateway;
    }

    @Override
    public List<AssessmentAttempt> execute(UUID assessmentId, UUID requesterId) {
        // 1. Buscar e validar a avaliação
        Assessment assessment = assessmentGateway.findById(assessmentId)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment with ID " + assessmentId + " not found."));

        // 2. Validar propriedade
        if (!(assessment instanceof PersonalizedAssessment personalizedAssessment)) {
            throw new AuthorizationException("This type of assessment does not support viewing attempts by creator.");
        }

        User requester = userGateway.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("Requester with ID " + requesterId + " not found."));

        if (requester.getRole() == UserRole.ADMIN) {
            return attemptGateway.findSummariesByAssessmentId(assessmentId);
        }

        if (requester.getRole() != UserRole.RECRUITER
                || personalizedAssessment.getCreatedBy() == null
                || !personalizedAssessment.getCreatedBy().getId().equals(requesterId)) {
            throw new AuthorizationException("You do not have permission to view attempts for this assessment.");
        }

        // 3. Buscar e retornar as tentativas
        return attemptGateway.findSummariesByAssessmentId(assessmentId);
    }
}
