package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.exceptions.assessment.InvalidAttemptStatusException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import com.lia.liaprove.core.usecases.assessments.EvaluateAssessmentAttemptUseCase;

import java.util.UUID;

/**
 * Implementação do caso de uso para avaliar manualmente um tentativa de avaliação.
 */
public class EvaluateAssessmentAttemptUseCaseImpl implements EvaluateAssessmentAttemptUseCase {

    private final AssessmentAttemptGateway attemptGateway;
    private final UserGateway userGateway;

    public EvaluateAssessmentAttemptUseCaseImpl(AssessmentAttemptGateway attemptGateway, UserGateway userGateway) {
        this.attemptGateway = attemptGateway;
        this.userGateway = userGateway;
    }

    @Override
    public AssessmentAttempt execute(UUID attemptId, UUID requesterId, AssessmentAttemptStatus finalStatus) {
        // 1. Validar o status de entrada
        if (finalStatus != AssessmentAttemptStatus.APPROVED && finalStatus != AssessmentAttemptStatus.FAILED) {
            throw new IllegalArgumentException("Final status must be APPROVED or FAILED.");
        }

        // 2. Buscar a tentativa
        AssessmentAttempt attempt = attemptGateway.findById(attemptId)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment attempt with ID " + attemptId + " not found."));

        // 3. Autorizar o solicitante
        authorizeRequester(requesterId, attempt);

        // 4. Validar o status atual da tentativa
        if (attempt.getStatus() != AssessmentAttemptStatus.COMPLETED) {
            throw new InvalidAttemptStatusException("Only attempts with status COMPLETED can be finalized.");
        }

        // 5. Atualizar o status
        attempt.setStatus(finalStatus);

        // 6. Persistir e retornar
        return attemptGateway.save(attempt);
    }
    
    private void authorizeRequester(UUID requesterId, AssessmentAttempt attempt) {
        User requester = userGateway.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("Requester with ID " + requesterId + " not found."));

        // Admin pode finalizar qualquer tentativa
        if (requester.getRole() == UserRole.ADMIN) {
            return;
        }

        // Recrutador só pode finalizar tentativas de suas próprias avaliações
        Assessment assessment = attempt.getAssessment();
        if (!(assessment instanceof PersonalizedAssessment personalizedAssessment)) {
            throw new AuthorizationException("This attempt does not belong to a personalized assessment.");
        }

        if (personalizedAssessment.getCreatedBy() == null || !personalizedAssessment.getCreatedBy().getId().equals(requesterId)) {
            throw new AuthorizationException("You do not have permission to finalize this assessment attempt.");
        }
    }
}
