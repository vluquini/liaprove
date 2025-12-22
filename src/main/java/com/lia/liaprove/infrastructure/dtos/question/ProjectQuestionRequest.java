package com.lia.liaprove.infrastructure.dtos.question;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("PROJECT")
public class ProjectQuestionRequest extends QuestionRequest {
//    @Size(max = 500)
//    private String projectUrl;
}
