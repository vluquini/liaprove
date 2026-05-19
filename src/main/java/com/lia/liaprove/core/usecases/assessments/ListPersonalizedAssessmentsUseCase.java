package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;

import java.util.List;
import java.util.UUID;

public interface ListPersonalizedAssessmentsUseCase {
    List<PersonalizedAssessment> execute(UUID requesterId);
}
