package com.lia.liaprove.infrastructure.entities.metrics;

import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@DiscriminatorValue("ASSESSMENT")
@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackAssessmentEntity extends FeedbackEntity {
    @Column(name = "assessment_attempt_id")
    private UUID assessmentAttemptId;

    @OneToMany(mappedBy = "feedbackAssessment", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderColumn(name = "ord_index")
    private List<FeedbackAssessmentReactionEntity> reactions = new ArrayList<>();

    public void addReaction(FeedbackAssessmentReactionEntity reaction) {
        this.reactions.add(reaction);
        reaction.setFeedbackAssessment(this);
    }
}
