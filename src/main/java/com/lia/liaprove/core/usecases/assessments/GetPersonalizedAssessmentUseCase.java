package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;

import java.util.UUID;

public interface GetPersonalizedAssessmentUseCase {
    PersonalizedAssessment execute(UUID assessmentId, UUID requesterId);
}
