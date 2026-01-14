package com.lia.liaprove.infrastructure.entities.metrics;

import com.lia.liaprove.core.domain.metrics.ReactionType;
import com.lia.liaprove.infrastructure.entities.users.UserEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "feedback_reaction")
@Data
public class FeedbackReactionEntity {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private ReactionType type;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_question_id", nullable = false)
    private FeedbackQuestionEntity feedbackQuestion; // Back-reference to the parent feedback
}
