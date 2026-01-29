package com.lia.liaprove.infrastructure.dtos.question;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("PROJECT")
public class SubmitProjectQuestionRequest extends SubmitQuestionRequest {}
