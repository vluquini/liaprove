package com.lia.liaprove.infrastructure.dtos.question;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(
                value = SubmitMultipleChoiceQuestionRequest.class,
                name = "MULTIPLE_CHOICE"
        ),
        @JsonSubTypes.Type(
                value = SubmitProjectQuestionRequest.class,
                name = "PROJECT"
        )
})
public abstract class SubmitQuestionRequest {
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

    private RelevanceLevel relevanceByLLM;
}
