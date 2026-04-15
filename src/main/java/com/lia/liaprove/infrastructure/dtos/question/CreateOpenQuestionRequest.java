package com.lia.liaprove.infrastructure.dtos.question;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class CreateOpenQuestionRequest {
    @NotBlank
    @Size(min = 10, max = 255)
    private String title;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String description;

    @NotNull
    private Set<KnowledgeArea> knowledgeAreas = new HashSet<>();

    @NotNull
    private DifficultyLevel difficultyByCommunity;

    @NotNull
    private RelevanceLevel relevanceByCommunity;

    @NotBlank
    @Size(min = 10, max = 2000)
    private String guideline;

    @NotNull
    private OpenQuestionVisibility visibility;
}
