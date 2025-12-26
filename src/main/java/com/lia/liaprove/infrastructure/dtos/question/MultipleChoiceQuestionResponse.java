package com.lia.liaprove.infrastructure.dtos.question;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MultipleChoiceQuestionResponse extends QuestionResponse {
    private List<AlternativeResponseDto> alternatives;
}
