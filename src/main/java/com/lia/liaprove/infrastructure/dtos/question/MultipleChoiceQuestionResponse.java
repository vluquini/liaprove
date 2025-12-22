package com.lia.liaprove.infrastructure.dtos.question;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MultipleChoiceQuestionResponse extends QuestionResponse {
    private UUID correctAlternativeId;
    private List<AlternativeResponseDto> alternatives;
}
