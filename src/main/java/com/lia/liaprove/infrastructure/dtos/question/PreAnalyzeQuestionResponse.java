package com.lia.liaprove.infrastructure.dtos.question;

import java.util.List;

public record PreAnalyzeQuestionResponse(
        List<String> languageSuggestions,
        List<String> biasOrAmbiguityWarnings,
        List<String> distractorSuggestions,
        String difficultyLevelByLLM,
        List<String> topicConsistencyNotes
) {}
