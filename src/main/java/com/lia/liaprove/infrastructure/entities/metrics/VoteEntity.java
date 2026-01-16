package com.lia.liaprove.infrastructure.entities.metrics;

import com.lia.liaprove.core.domain.metrics.VoteType;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.users.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vote")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteEntity {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionEntity question;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", nullable = false)
    private VoteType voteType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
