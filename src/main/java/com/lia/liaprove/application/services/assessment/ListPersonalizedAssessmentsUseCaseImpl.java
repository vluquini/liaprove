package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import com.lia.liaprove.core.exceptions.user.UserNotFoundException;
import com.lia.liaprove.core.usecases.assessments.ListPersonalizedAssessmentsUseCase;

import java.util.List;
import java.util.UUID;

public class ListPersonalizedAssessmentsUseCaseImpl implements ListPersonalizedAssessmentsUseCase {

    private final AssessmentGateway assessmentGateway;
    private final UserGateway userGateway;

    public ListPersonalizedAssessmentsUseCaseImpl(AssessmentGateway assessmentGateway, UserGateway userGateway) {
        this.assessmentGateway = assessmentGateway;
        this.userGateway = userGateway;
    }

    @Override
    public List<PersonalizedAssessment> execute(UUID requesterId) {
        User requester = userGateway.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException("Requester with ID " + requesterId + " not found."));

        if (requester.getRole() == UserRole.ADMIN) {
            return assessmentGateway.findAllPersonalizedAssessments();
        }

        if (requester.getRole() != UserRole.RECRUITER) {
            throw new AuthorizationException("Only recruiters and admins can list personalized assessments.");
        }

        return assessmentGateway.findPersonalizedAssessmentsByCreatorId(requesterId);
    }
}
