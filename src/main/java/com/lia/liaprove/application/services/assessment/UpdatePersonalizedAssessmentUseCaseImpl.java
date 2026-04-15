package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import com.lia.liaprove.core.usecases.assessments.UpdatePersonalizedAssessmentUseCase;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do caso de uso para atualizar uma Avaliação Personalizada.
 */
public class UpdatePersonalizedAssessmentUseCaseImpl implements UpdatePersonalizedAssessmentUseCase {

    private final AssessmentGateway assessmentGateway;
    private final UserGateway userGateway;

    public UpdatePersonalizedAssessmentUseCaseImpl(AssessmentGateway assessmentGateway, UserGateway userGateway) {
        this.assessmentGateway = assessmentGateway;
        this.userGateway = userGateway;
    }

    @Override
    public Assessment execute(UUID assessmentId, UUID requesterId, Optional<LocalDateTime> expirationDate,
                              Optional<Integer> maxAttempts, Optional<PersonalizedAssessmentStatus> status,
                              Optional<AssessmentCriteriaWeights> criteriaWeights) {

        // 1. Buscar a avaliação
        Assessment assessment = assessmentGateway.findById(assessmentId)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment with ID " + assessmentId + " not found."));

        if (!(assessment instanceof PersonalizedAssessment personalizedAssessment)) {
            throw new IllegalArgumentException("Only personalized assessments can be updated through this use case.");
        }

        // 2. Autorizar
        authorizeRequester(requesterId, personalizedAssessment);

        // 3. Aplicar atualizações
        expirationDate.ifPresent(personalizedAssessment::setExpirationDate);
        maxAttempts.ifPresent(personalizedAssessment::setMaxAttempts);
        status.ifPresent(personalizedAssessment::setStatus);
        criteriaWeights.ifPresent(personalizedAssessment::setCriteriaWeights);

        // 4. Persistir e retornar
        return assessmentGateway.save(personalizedAssessment);
    }

    private void authorizeRequester(UUID requesterId, PersonalizedAssessment assessment) {
        User requester = userGateway.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("Requester with ID " + requesterId + " not found."));

        if (requester.getRole() == UserRole.ADMIN) {
            return; // Admins podem editar
        }

        if (assessment.getCreatedBy() == null || !assessment.getCreatedBy().getId().equals(requesterId)) {
            throw new AuthorizationException("You do not have permission to update this assessment.");
        }
    }
}
