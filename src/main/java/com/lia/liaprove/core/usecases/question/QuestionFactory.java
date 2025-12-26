package com.lia.liaprove.core.usecases.question;

import com.lia.liaprove.application.services.question.QuestionCreateDto;
import com.lia.liaprove.core.domain.question.*;

/**
 * Interface Factory para a criação de diferentes tipos de questões.
 */
public interface QuestionFactory {
    Question createMultipleChoice(QuestionCreateDto dto);
    Question createProject(QuestionCreateDto dto);
}
