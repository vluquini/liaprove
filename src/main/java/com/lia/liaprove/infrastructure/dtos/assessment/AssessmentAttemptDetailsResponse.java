package com.lia.liaprove.infrastructure.dtos.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.infrastructure.dtos.user.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AssessmentAttemptDetailsResponse(
        UUID attemptId,
        AssessmentAttemptStatus status,
        Integer accuracyRate,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        AssessmentSummary assessment,
        UserResponseDto candidate,
        AssessmentExplainabilityResponse explainability,
        List<AttemptQuestionDetailsResponse> questions
) {

    public record AssessmentSummary(
            UUID id,
            String title,
            String description,
            Long evaluationTimerMinutes,
            AssessmentCriteriaWeightsResponse criteriaWeights,
            JobDescriptionAnalysisResponse jobDescriptionAnalysis
    ) {}

    public record AttemptQuestionDetailsResponse(
            UUID id,
            String title,
            String description,
            String guideline,
            List<AlternativeResponse> alternatives,
            AnswerResponse answer
    ) {}

    public record AlternativeResponse(
            UUID id,
            String text
    ) {}

    public record AnswerResponse(
            UUID questionId,
            UUID selectedAlternativeId,
            String projectUrl,
            String textResponse
    ) {}
}
