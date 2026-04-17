package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
                AssessmentCriteriaWeights.defaultWeights(),
                Optional.empty()
        );
    }

    default PersonalizedAssessment execute(UUID creatorId, String title, String description, List<UUID> questionIds,
                                           LocalDateTime expirationDate, int maxAttempts, long evaluationTimerMinutes,
                                           AssessmentCriteriaWeights criteriaWeights) {
        return execute(
                creatorId,
                title,
                description,
                questionIds,
                expirationDate,
                maxAttempts,
                evaluationTimerMinutes,
                criteriaWeights,
                Optional.empty()
        );
    }

    PersonalizedAssessment execute(UUID creatorId, String title, String description, List<UUID> questionIds,
                                   LocalDateTime expirationDate, int maxAttempts, long evaluationTimerMinutes,
                                   AssessmentCriteriaWeights criteriaWeights,
                                   Optional<JobDescriptionAnalysis> jobDescriptionAnalysis);
}
