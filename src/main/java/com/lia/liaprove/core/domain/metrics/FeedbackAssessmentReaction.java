package com.lia.liaprove.core.domain.metrics;

import com.lia.liaprove.core.domain.user.User;

/**
 * @deprecated Temporary compatibility bridge. Assessment feedback reactions are now represented by
 * {@link FeedbackReaction}; this type should be removed when infrastructure mappers and DTOs are unified.
 */
@Deprecated(forRemoval = true)
public class FeedbackAssessmentReaction extends FeedbackReaction {

    public FeedbackAssessmentReaction() {
        super();
    }

    public FeedbackAssessmentReaction(User user, ReactionType type) {
        super(user, type);
    }
}
