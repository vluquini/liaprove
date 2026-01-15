package com.lia.liaprove.infrastructure.dtos.metrics;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeedbackRequest {
    @NotBlank(message = "Comment cannot be empty")
    private String comment;

    @NotNull(message = "Difficulty level cannot be null")
    private DifficultyLevel difficultyLevel;

    @NotNull(message = "Knowledge area cannot be null")
    private KnowledgeArea knowledgeArea;

    @NotNull(message = "Relevance level cannot be null")
    private RelevanceLevel relevanceLevel;
}
