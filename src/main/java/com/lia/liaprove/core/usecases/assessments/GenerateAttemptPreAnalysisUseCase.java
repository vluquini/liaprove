package com.lia.liaprove.core.usecases.assessments;

import com.lia.liaprove.core.domain.assessment.AttemptPreAnalysis;

import java.util.UUID;

public interface GenerateAttemptPreAnalysisUseCase {
    AttemptPreAnalysis execute(UUID attemptId, UUID requesterId);
}
