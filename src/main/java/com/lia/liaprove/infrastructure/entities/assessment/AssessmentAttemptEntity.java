package com.lia.liaprove.infrastructure.entities.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.users.UserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "assessment_attempts")
@Data
public class AssessmentAttemptEntity {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private AssessmentEntity assessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "assessment_attempt_questions",
            joinColumns = @JoinColumn(name = "attempt_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<QuestionEntity> questions = new ArrayList<>();

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderColumn(name = "ord_index")
    private List<AnswerEntity> answers = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime finishedAt;

    @Column
    private Integer accuracyRate;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "certificate_id")
    private CertificateEntity certificate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AssessmentAttemptStatus status;

    public void addAnswer(AnswerEntity answer) {
        if (answer == null) {
            return;
        }
        answers.add(answer);
        answer.setAttempt(this);
    }

    public void removeAnswer(AnswerEntity answer) {
        if (answer == null) {
            return;
        }
        answers.remove(answer);
        answer.setAttempt(null);
    }
}

