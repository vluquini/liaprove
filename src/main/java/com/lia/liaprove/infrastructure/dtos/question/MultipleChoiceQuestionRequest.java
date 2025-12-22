package com.lia.liaprove.infrastructure.dtos.question;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.lia.liaprove.infrastructure.validation.ExactlyOneCorrectAlternative;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("MULTIPLE_CHOICE")
public class MultipleChoiceQuestionRequest extends QuestionRequest {
    @Valid
    @NotNull
    @Size(min = 3, max = 5)
    @ExactlyOneCorrectAlternative
    private List<AlternativeRequestDto> alternatives;
}
