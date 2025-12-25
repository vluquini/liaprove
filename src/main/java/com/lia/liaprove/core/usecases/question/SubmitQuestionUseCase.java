package com.lia.liaprove.core.usecases.question;

import com.lia.liaprove.application.services.question.QuestionCreateDto;
import com.lia.liaprove.core.domain.question.*;

/**
 * Interface para o caso de uso de submissão de uma nova questão.
 * Define o contrato para a criação de questões no sistema.
 */
public interface SubmitQuestionUseCase {
    Question createMultipleChoice(QuestionCreateDto dto);
    Question createProject(QuestionCreateDto dto);
}
