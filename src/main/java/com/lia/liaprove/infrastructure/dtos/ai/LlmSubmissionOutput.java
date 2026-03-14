package com.lia.liaprove.infrastructure.dtos.ai;

import java.util.List;

public record LlmSubmissionOutput(
        String title,
        String description,
        List<LlmAlternative> alternatives,
        String relevanceByLLM
) {}
