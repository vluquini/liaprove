package com.lia.liaprove.infrastructure.dtos.ai;

import com.lia.liaprove.core.usecases.question.PrepareQuestionSubmissionUseCase;

import java.util.List;

public record SubmissionQuestionDraftInput(
        String title,
        String description,
        Object knowledgeAreas,
        Object difficultyByCommunity,
        Object relevanceByCommunity,
        List<PrepareQuestionSubmissionUseCase.AlternativeInput> alternatives
) {}
