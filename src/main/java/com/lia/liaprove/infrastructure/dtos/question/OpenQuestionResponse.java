package com.lia.liaprove.infrastructure.dtos.question;

import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OpenQuestionResponse extends QuestionResponse {
    private String guideline;
    private OpenQuestionVisibility visibility;
}
