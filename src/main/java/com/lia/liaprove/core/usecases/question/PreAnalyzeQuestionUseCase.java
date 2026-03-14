package com.lia.liaprove.core.usecases.question;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.RelevanceLevel;

import java.util.List;
import java.util.Set;

/**
 * Caso de uso para pré-análise de questões com IA antes da submissão final.
 * Este fluxo não persiste dados da questão.
 */
public interface PreAnalyzeQuestionUseCase {

    PreAnalysisResult execute(PreAnalysisCommand command);

    record PreAnalysisCommand(
            String title,
            String description,
            Set<KnowledgeArea> knowledgeAreas,
            DifficultyLevel difficultyByCommunity,
            RelevanceLevel relevanceByCommunity,
            List<String> alternatives
    ) {}

    record PreAnalysisResult(
            List<String> languageSuggestions,
            List<String> biasOrAmbiguityWarnings,
            List<String> distractorSuggestions,
            String difficultyLevelByLLM,
            List<String> topicConsistencyNotes
    ) {}
}
