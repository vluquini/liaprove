package com.lia.liaprove.infrastructure.dtos.assessment;

import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.infrastructure.dtos.question.QuestionSummaryResponse;
import com.lia.liaprove.infrastructure.dtos.user.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PersonalizedAssessmentDetailsResponse(
        UUID id,
        String title,
        String description,
        LocalDateTime creationDate,
        Long evaluationTimerMinutes,
        LocalDateTime expirationDate,
        int totalAttempts,
        int maxAttempts,
        String shareableToken,
        PersonalizedAssessmentStatus status,
        UserResponseDto createdBy,
        AssessmentCriteriaWeightsResponse criteriaWeights,
        JobDescriptionAnalysisResponse jobDescriptionAnalysis,
        List<QuestionSummaryResponse> questions
) {
}
