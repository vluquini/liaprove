package com.lia.liaprove.core.usecases.user;

import java.util.UUID;

public interface ModerateFeedbackUseCase {
    void removeFeedback(UUID feedbackId, UUID adminId, String reason);
    void editFeedback(UUID feedbackId, String newComment, UUID adminId);
}

