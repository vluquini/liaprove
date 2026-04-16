package com.lia.liaprove.infrastructure.dtos.assessment;

import com.lia.liaprove.core.domain.question.KnowledgeArea;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record CreatePersonalizedAssessmentRequest(
    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Description is required")
    String description,

    @NotEmpty(message = "At least one question must be selected")
    List<UUID> questionIds,

    @NotNull(message = "Expiration date is required")
    @Future(message = "Expiration date must be in the future")
    LocalDateTime expirationDate,

    @Min(value = 1, message = "Max attempts must be at least 1")
    int maxAttempts,

    @Min(value = 5, message = "Evaluation timer must be at least 5 minutes")
    long evaluationTimerMinutes,

    @Min(value = 0, message = "Hard skills weight must be non-negative")
    Integer hardSkillsWeight,

    @Min(value = 0, message = "Soft skills weight must be non-negative")
    Integer softSkillsWeight,

    @Min(value = 0, message = "Experience weight must be non-negative")
    Integer experienceWeight,

    @Valid
    JobDescriptionAnalysisSnapshotRequest jobDescriptionAnalysis
) {
    public CreatePersonalizedAssessmentRequest(
            String title,
            String description,
            List<UUID> questionIds,
            LocalDateTime expirationDate,
            int maxAttempts,
            long evaluationTimerMinutes,
            Integer hardSkillsWeight,
            Integer softSkillsWeight,
            Integer experienceWeight
    ) {
        this(
                title,
                description,
                questionIds,
                expirationDate,
                maxAttempts,
                evaluationTimerMinutes,
                hardSkillsWeight,
                softSkillsWeight,
                experienceWeight,
                null
        );
    }

    public CreatePersonalizedAssessmentRequest(
            String title,
            String description,
            List<UUID> questionIds,
            LocalDateTime expirationDate,
            int maxAttempts,
            long evaluationTimerMinutes
    ) {
        this(title, description, questionIds, expirationDate, maxAttempts, evaluationTimerMinutes, null, null, null, null);
    }

    public record JobDescriptionAnalysisSnapshotRequest(
            String originalJobDescription,
            Set<KnowledgeArea> suggestedKnowledgeAreas,
            List<String> suggestedHardSkills,
            List<String> suggestedSoftSkills,
            Integer suggestedHardSkillsWeight,
            Integer suggestedSoftSkillsWeight,
            Integer suggestedExperienceWeight
    ) {}
}
