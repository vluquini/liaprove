package com.lia.liaprove.infrastructure.dtos.question;

import com.lia.liaprove.core.domain.question.RelevanceLevel;

import java.util.List;

public record PreAnalyzeQuestionResponse(
        RelevanceLevel relevanceByLLM,
        List<String> languageSuggestions,
        List<String> biasOrAmbiguityWarnings,
        List<String> distractorSuggestions,
        String difficultyLevelByLLM,
        List<String> topicConsistencyNotes
) {
}
