package com.lia.liaprove.infrastructure.entities.question;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "questions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class QuestionEntity {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID authorId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000, unique = true)
    private String description;

    @ElementCollection(targetClass = KnowledgeArea.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "question_knowledge_areas", joinColumns = @JoinColumn(name = "question_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "knowledge_area", nullable = false)
    private Set<KnowledgeArea> knowledgeAreas = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficultyByCommunity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelevanceLevel relevanceByCommunity;

    @Column(nullable = false)
    private LocalDateTime submissionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelevanceLevel relevanceByLLM;

    @Column(nullable = false)
    private int recruiterUsageCount;
}
