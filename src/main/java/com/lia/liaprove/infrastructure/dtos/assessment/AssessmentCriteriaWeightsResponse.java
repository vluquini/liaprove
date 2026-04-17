package com.lia.liaprove.infrastructure.dtos.assessment;

public record AssessmentCriteriaWeightsResponse(
        int hardSkillsWeight,
        int softSkillsWeight,
        int experienceWeight
) {}
