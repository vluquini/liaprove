package com.lia.liaprove.infrastructure.dtos.ai;

import java.util.List;

public record AcceptedSuggestionsInput(
        List<String> languageSuggestions,
        List<String> biasOrAmbiguityWarnings,
        List<String> distractorSuggestions,
        String difficultyLevelByLLM,
        List<String> topicConsistencyNotes
) {}
