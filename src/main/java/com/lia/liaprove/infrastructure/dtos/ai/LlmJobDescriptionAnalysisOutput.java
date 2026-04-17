package com.lia.liaprove.infrastructure.dtos.ai;

import java.util.List;

public record LlmJobDescriptionAnalysisOutput(
        String originalJobDescription,
        List<String> suggestedKnowledgeAreas,
        List<String> suggestedHardSkills,
        List<String> suggestedSoftSkills,
        Integer suggestedHardSkillsWeight,
        Integer suggestedSoftSkillsWeight,
        Integer suggestedExperienceWeight
) {}
