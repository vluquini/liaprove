package com.lia.liaprove.infrastructure.dtos.question;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProjectQuestionResponse extends QuestionResponse {
    private String projectUrl;
}
