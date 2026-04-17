package com.lia.liaprove.infrastructure.entities.assessment;

import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.infrastructure.entities.user.UserRecruiterEntity;
import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@DiscriminatorValue("PERSONALIZED")
@Data
@EqualsAndHashCode(callSuper = true)
public class PersonalizedAssessmentEntity extends AssessmentEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_recruiter_id")
    private UserRecruiterEntity createdBy;

    @Column
    private LocalDateTime expirationDate;

    @Column
    private int totalAttempts;

    @Column
    private int maxAttempts;

    @Column(unique = true)
    private String shareableToken;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private PersonalizedAssessmentStatus status;

    @Column
    private int hardSkillsWeight = 34;

    @Column
    private int softSkillsWeight = 33;

    @Column
    private int experienceWeight = 33;

    @Lob
    @Column
    private String originalJobDescription;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "personalized_assessment_job_description_knowledge_areas",
            joinColumns = @JoinColumn(name = "personalized_assessment_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "knowledge_area", length = 64, nullable = false)
    private Set<KnowledgeArea> suggestedKnowledgeAreas;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "personalized_assessment_job_description_hard_skills",
            joinColumns = @JoinColumn(name = "personalized_assessment_id")
    )
    @Column(name = "skill", nullable = false)
    @OrderColumn(name = "skill_order")
    private List<String> suggestedHardSkills;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "personalized_assessment_job_description_soft_skills",
            joinColumns = @JoinColumn(name = "personalized_assessment_id")
    )
    @Column(name = "skill", nullable = false)
    @OrderColumn(name = "skill_order")
    private List<String> suggestedSoftSkills;

    @Column
    private Integer suggestedHardSkillsWeight;

    @Column
    private Integer suggestedSoftSkillsWeight;

    @Column
    private Integer suggestedExperienceWeight;
}

