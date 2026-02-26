package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.usecases.assessments.ListAttemptsForMyAssessmentUseCase;

import java.util.List;
import java.util.UUID;

/**
 * Implementação do caso de uso para listar tentativas de uma avaliação para seu criador.
 */
public class ListAttemptsForMyAssessmentUseCaseImpl implements ListAttemptsForMyAssessmentUseCase {

    private final AssessmentGateway assessmentGateway;
    private final AssessmentAttemptGateway attemptGateway;

    public ListAttemptsForMyAssessmentUseCaseImpl(AssessmentGateway assessmentGateway, AssessmentAttemptGateway attemptGateway) {
        this.assessmentGateway = assessmentGateway;
        this.attemptGateway = attemptGateway;
    }

    @Override
    public List<AssessmentAttempt> execute(UUID assessmentId, UUID recruiterId) {
        // 1. Buscar e validar a avaliação
        Assessment assessment = assessmentGateway.findById(assessmentId)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment with ID " + assessmentId + " not found."));

        // 2. Validar propriedade
        if (!(assessment instanceof PersonalizedAssessment personalizedAssessment)) {
            throw new AuthorizationException("This type of assessment does not support viewing attempts by creator.");
        }

        if (personalizedAssessment.getCreatedBy() == null || !personalizedAssessment.getCreatedBy().getId().equals(recruiterId)) {
            throw new AuthorizationException("You do not have permission to view attempts for this assessment.");
        }

        // 3. Buscar e retornar as tentativas
        return attemptGateway.findByAssessmentId(assessmentId);
    }
}
