package com.lia.liaprove.infrastructure.dtos.assessment;

import java.util.List;

public record AssessmentExplainabilityResponse(
        int totalQuestions,
        int answeredQuestions,
        int multipleChoiceQuestions,
        int openQuestions,
        int projectQuestions,
        String candidateExperienceLevel,
        List<String> candidateHardSkills,
        List<String> candidateSoftSkills,
        AssessmentCriteriaWeightsResponse criteriaWeights
) {}
