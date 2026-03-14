package com.lia.liaprove.infrastructure.dtos.ai;

import java.util.List;

public record PromptInput(
        String title,
        String description,
        Object knowledgeAreas,
        Object difficultyByCommunity,
        Object relevanceByCommunity,
        List<String> alternatives
) {}
