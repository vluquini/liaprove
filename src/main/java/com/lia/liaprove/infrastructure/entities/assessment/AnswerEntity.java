package com.lia.liaprove.infrastructure.entities.assessment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "assessment_answers")
@Data
public class AnswerEntity {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_attempt_id", nullable = false)
    private AssessmentAttemptEntity attempt;

    @Column(nullable = false)
    private UUID questionId;

    @Column
    private UUID selectedAlternativeId;

    @Column(length = 500)
    private String projectUrl;
}

