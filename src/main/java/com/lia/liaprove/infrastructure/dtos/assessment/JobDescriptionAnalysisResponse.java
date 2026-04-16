package com.lia.liaprove.infrastructure.dtos.assessment;

import java.util.List;
import java.util.Set;

public record JobDescriptionAnalysisResponse(
        String originalJobDescription,
        Set<String> suggestedKnowledgeAreas,
        List<String> suggestedHardSkills,
        List<String> suggestedSoftSkills,
        AssessmentCriteriaWeightsResponse suggestedCriteriaWeights
) {}
