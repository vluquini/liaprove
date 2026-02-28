package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.assessment.AssessmentInUseException;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import com.lia.liaprove.core.usecases.assessments.DeletePersonalizedAssessmentUseCase;

import java.util.UUID;

/**
 * Implementação do caso de uso para excluir uma Avaliação Personalizada.
 */
public class DeletePersonalizedAssessmentUseCaseImpl implements DeletePersonalizedAssessmentUseCase {

    private final AssessmentGateway assessmentGateway;
    private final AssessmentAttemptGateway attemptGateway;
    private final UserGateway userGateway;

    public DeletePersonalizedAssessmentUseCaseImpl(AssessmentGateway assessmentGateway,
                                                   AssessmentAttemptGateway attemptGateway,
                                                   UserGateway userGateway) {
        this.assessmentGateway = assessmentGateway;
        this.attemptGateway = attemptGateway;
        this.userGateway = userGateway;
    }

    @Override
    public void execute(UUID assessmentId, UUID requesterId) {
        // 1. Buscar a avaliação
        Assessment assessment = assessmentGateway.findById(assessmentId)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment with ID " + assessmentId + " not found."));

        // 2. Autorizar o solicitante
        authorizeRequester(requesterId, assessment);

        // 3. Verificar se a avaliação está em uso
        long attemptCount = attemptGateway.countByAssessmentId(assessmentId);
        if (attemptCount > 0) {
            throw new AssessmentInUseException("This assessment cannot be deleted because it has " + attemptCount + " attempt(s).");
        }

        // 4. Executar a exclusão
        assessmentGateway.deletePersonalizedAssessmentById(assessmentId);
    }

    private void authorizeRequester(UUID requesterId, Assessment assessment) {
        User requester = userGateway.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("Requester with ID " + requesterId + " not found."));

        if (requester.getRole() == UserRole.ADMIN) {
            return; // Admins podem excluir qualquer avaliação (que não esteja em uso)
        }

        if (!(assessment instanceof PersonalizedAssessment personalizedAssessment)) {
            throw new AuthorizationException("Only personalized assessments can be deleted.");
        }

        if (personalizedAssessment.getCreatedBy() == null || !personalizedAssessment.getCreatedBy().getId().equals(requesterId)) {
            throw new AuthorizationException("You do not have permission to delete this assessment.");
        }
    }
}
