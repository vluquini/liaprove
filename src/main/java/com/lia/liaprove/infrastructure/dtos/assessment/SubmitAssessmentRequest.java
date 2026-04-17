package com.lia.liaprove.infrastructure.dtos.assessment;

import java.util.List;
import java.util.UUID;

public record SubmitAssessmentRequest(
    List<QuestionAnswerRequest> answers
) {
    public record QuestionAnswerRequest(
        UUID questionId,
        UUID selectedAlternativeId,
        String projectUrl,
        String textResponse
    ) {
        public QuestionAnswerRequest(UUID questionId, UUID selectedAlternativeId, String projectUrl) {
            this(questionId, selectedAlternativeId, projectUrl, null);
        }
    }
}
