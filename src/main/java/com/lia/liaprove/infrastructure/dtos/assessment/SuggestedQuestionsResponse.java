package com.lia.liaprove.infrastructure.dtos.assessment;

import java.util.List;

public record SuggestedQuestionsResponse(
    List<ScoredQuestionResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean last
) {}
