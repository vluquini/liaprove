package com.lia.liaprove.infrastructure.dtos.question;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceQuestionResponse.class, name = "MULTIPLE_CHOICE"),
        @JsonSubTypes.Type(value = ProjectQuestionResponse.class, name = "PROJECT")
})
public abstract class QuestionResponse {
    private UUID id;
    private UUID authorId;
    private String title;
    private String description;
    private Set<KnowledgeArea> knowledgeAreas = new HashSet<>();
    private DifficultyLevel difficultyByCommunity;
    private LocalDateTime submissionDate;
    private QuestionStatus status;
}
