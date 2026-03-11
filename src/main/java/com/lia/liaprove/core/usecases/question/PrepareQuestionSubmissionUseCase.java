package com.lia.liaprove.core.usecases.question;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.RelevanceLevel;

import java.util.List;
import java.util.Set;

public interface PrepareQuestionSubmissionUseCase {

    PreparedQuestion execute(PreparationCommand command);

    record PreparationCommand(
            String title,
            String description,
            Set<KnowledgeArea> knowledgeAreas,
            DifficultyLevel difficultyByCommunity,
            RelevanceLevel relevanceByCommunity,
            List<AlternativeInput> alternatives,
            List<String> acceptedLanguageSuggestions,
            List<String> acceptedBiasOrAmbiguityWarnings,
            List<String> acceptedDistractorSuggestions,
            String acceptedDifficultyLevelByLLM,
            List<String> acceptedTopicConsistencyNotes
    ) {}

    record AlternativeInput(
            String text,
            boolean correct
    ) {}

    record PreparedQuestion(
            String title,
            String description,
            List<AlternativeInput> alternatives,
            RelevanceLevel relevanceByLLM
    ) {}
}
