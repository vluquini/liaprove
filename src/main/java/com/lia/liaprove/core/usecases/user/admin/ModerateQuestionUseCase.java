package com.lia.liaprove.core.usecases.user.admin;

import com.lia.liaprove.core.domain.question.QuestionStatus;

import java.util.UUID;

/**
 * Aprovar, rejeitar ou forçar estado de uma Question.
 */
public interface ModerateQuestionUseCase {
    void approveQuestion(UUID questionId, UUID adminId);
    void rejectQuestion(UUID questionId, UUID adminId, String reason);
    void setQuestionStatus(UUID questionId, QuestionStatus status, UUID adminId, String reason);
}

