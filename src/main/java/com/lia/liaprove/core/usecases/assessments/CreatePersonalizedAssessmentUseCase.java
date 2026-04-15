package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface específica para a criação de avaliações personalizadas por recrutadores.
 */
public interface CreatePersonalizedAssessmentUseCase {

    default PersonalizedAssessment execute(UUID creatorId, String title, String description, List<UUID> questionIds,
                                           LocalDateTime expirationDate, int maxAttempts, long evaluationTimerMinutes) {
        return execute(
                creatorId,
                title,
                description,
                questionIds,
                expirationDate,
                maxAttempts,
                evaluationTimerMinutes,
                AssessmentCriteriaWeights.defaultWeights()
        );
    }

    PersonalizedAssessment execute(UUID creatorId, String title, String description, List<UUID> questionIds,
                                   LocalDateTime expirationDate, int maxAttempts, long evaluationTimerMinutes,
                                   AssessmentCriteriaWeights criteriaWeights);
}
