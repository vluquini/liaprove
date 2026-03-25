package com.lia.liaprove.infrastructure.entities.algorithms.genetic;

import com.lia.liaprove.core.domain.user.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "vote_multipliers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"scope", "role"}),
                @UniqueConstraint(columnNames = {"scope", "recruiter_id"})
        }
)
@Data
public class VoteMultiplierEntity {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private VoteMultiplierScope scope;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private UserRole role;

    @Column(name = "recruiter_id")
    private UUID recruiterId;

    @Column(nullable = false)
    private double multiplier;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
