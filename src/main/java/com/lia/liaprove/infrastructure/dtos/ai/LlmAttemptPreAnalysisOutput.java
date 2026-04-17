package com.lia.liaprove.infrastructure.dtos.ai;

import java.util.List;

public record LlmAttemptPreAnalysisOutput(
        String summary,
        List<String> strengths,
        List<String> attentionPoints,
        String finalExplanation
) {
}
