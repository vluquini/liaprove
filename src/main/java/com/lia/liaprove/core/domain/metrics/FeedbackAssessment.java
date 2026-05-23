package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Feedback deixado por um usuário sobre uma Assessment.
 *
 * - Associa-se a uma única Assessment (relação definida no construtor; não deve ser
 *   alterada depois da criação).
 * - Em regras de negócio, comentários de assessments podem ser privados (visíveis apenas
 *   ao recruiter e administradores) enquanto comentários de questões são públicos.
 */
public class FeedbackAssessment extends Feedback {
    private AssessmentAttempt assessmentAttempt;
    private Map<UUID, FeedbackAssessmentReaction> reactionsByUser = new LinkedHashMap<>();

    public FeedbackAssessment() {
        super();
    }

    public FeedbackAssessment(User user, AssessmentAttempt assessmentAttempt, String comment,
                              LocalDateTime submissionDate, boolean visible) {
        super(user, comment, submissionDate, visible);
        this.assessmentAttempt = assessmentAttempt;
    }

    public AssessmentAttempt getAssessmentAttempt() {
        return assessmentAttempt;
    }

    public void setAssessmentAttempt(AssessmentAttempt assessmentAttempt) {
        this.assessmentAttempt = assessmentAttempt;
    }

    public List<FeedbackAssessmentReaction> getReactions() {
        return List.copyOf(reactionsByUser.values());
    }

    public void setReactions(List<FeedbackAssessmentReaction> reactions) {
        if (!this.reactionsByUser.isEmpty()) {
            return;
        }
        if (reactions != null) {
            for (FeedbackAssessmentReaction reaction : reactions) {
                if (reaction != null && reaction.getUser() != null) {
                    this.reactionsByUser.put(reaction.getUser().getId(), reaction);
                }
            }
        }
    }

    public boolean manageReaction(User user, ReactionType type) {
        Objects.requireNonNull(user, "user");
        Objects.requireNonNull(type, "type");

        FeedbackAssessmentReaction existing = reactionsByUser.get(user.getId());

        if (existing == null) {
            FeedbackAssessmentReaction reaction = new FeedbackAssessmentReaction(user, type);
            reactionsByUser.put(user.getId(), reaction);
            touchUpdatedAt();
            return true;
        }

        if (existing.getType() == type) {
            reactionsByUser.remove(user.getId());
            touchUpdatedAt();
            return true;
        }

        existing.setType(type);
        touchUpdatedAt();
        return true;
    }
}
