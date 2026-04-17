package com.lia.liaprove.infrastructure.dtos.ai;

import java.util.List;
import java.util.UUID;

public record AttemptPreAnalysisInput(
        UUID attemptId,
        List<QuestionInput> supportedQuestions,
        List<String> ignoredQuestionTypes
) {
    public record QuestionInput(
            UUID questionId,
            String questionType,
            String title,
            String description,
            List<String> knowledgeAreas,
            String difficultyByCommunity,
            String relevanceByCommunity,
            String guideline,
            String visibility,
            List<AlternativeInput> alternatives
    ) {
    }

    public record AlternativeInput(
            UUID alternativeId,
            String text,
            boolean correct
    ) {
    }
}
