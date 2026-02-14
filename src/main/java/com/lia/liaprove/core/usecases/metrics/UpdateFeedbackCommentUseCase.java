package com.lia.liaprove.core.usecases.metrics;

import java.util.UUID;

public interface UpdateFeedbackCommentUseCase {
    void execute(UUID actorId, UUID feedbackId, String newComment);
}
