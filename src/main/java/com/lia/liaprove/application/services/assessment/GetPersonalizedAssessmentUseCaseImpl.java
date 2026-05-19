package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.Assessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import com.lia.liaprove.core.usecases.assessments.GetPersonalizedAssessmentUseCase;

import java.util.UUID;

public class GetPersonalizedAssessmentUseCaseImpl implements GetPersonalizedAssessmentUseCase {

    private final AssessmentGateway assessmentGateway;
    private final UserGateway userGateway;

    public GetPersonalizedAssessmentUseCaseImpl(AssessmentGateway assessmentGateway, UserGateway userGateway) {
        this.assessmentGateway = assessmentGateway;
        this.userGateway = userGateway;
    }

    @Override
    public PersonalizedAssessment execute(UUID assessmentId, UUID requesterId) {
        User requester = userGateway.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("Requester with ID " + requesterId + " not found."));

        Assessment assessment = assessmentGateway.findById(assessmentId)
                .orElseThrow(() -> new AssessmentNotFoundException("Assessment with ID " + assessmentId + " not found."));

        if (!(assessment instanceof PersonalizedAssessment personalizedAssessment)) {
            throw new AuthorizationException("Only personalized assessments can be accessed through this use case.");
        }

        if (requester.getRole() == UserRole.ADMIN) {
            return personalizedAssessment;
        }

        if (requester.getRole() != UserRole.RECRUITER
                || personalizedAssessment.getCreatedBy() == null
                || !personalizedAssessment.getCreatedBy().getId().equals(requesterId)) {
            throw new AuthorizationException("You do not have permission to access this assessment.");
        }

        return personalizedAssessment;
    }
}
