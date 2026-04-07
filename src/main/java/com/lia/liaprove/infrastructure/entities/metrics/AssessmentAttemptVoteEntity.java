package com.lia.liaprove.infrastructure.entities.metrics;

import com.lia.liaprove.core.domain.metrics.VoteType;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "assessment_attempt_votes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "assessment_attempt_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentAttemptVoteEntity {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_attempt_id", nullable = false)
    private AssessmentAttemptEntity assessmentAttempt;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", nullable = false)
    private VoteType voteType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
