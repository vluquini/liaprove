package com.lia.liaprove.infrastructure.dtos.assessment;

import com.lia.liaprove.core.domain.question.KnowledgeArea;
import jakarta.validation.constraints.AssertTrue;
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
    @AssertTrue(message = "Criteria weights must be either fully omitted or sum to 100.")
    public boolean isCriteriaWeightsValid() {
        return areOptionalWeightsValid(hardSkillsWeight, softSkillsWeight, experienceWeight);
    }

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
            @NotBlank(message = "Original job description is required")
            String originalJobDescription,
            Set<KnowledgeArea> suggestedKnowledgeAreas,
            List<String> suggestedHardSkills,
            List<String> suggestedSoftSkills,
            @Min(value = 0, message = "Suggested hard skills weight must be non-negative")
            Integer suggestedHardSkillsWeight,
            @Min(value = 0, message = "Suggested soft skills weight must be non-negative")
            Integer suggestedSoftSkillsWeight,
            @Min(value = 0, message = "Suggested experience weight must be non-negative")
            Integer suggestedExperienceWeight
    ) {
        @AssertTrue(message = "Suggested snapshot weights must be either fully omitted or sum to 100.")
        public boolean isSuggestedWeightsValid() {
            return areOptionalWeightsValid(
                    suggestedHardSkillsWeight,
                    suggestedSoftSkillsWeight,
                    suggestedExperienceWeight
            );
        }
    }

    private static boolean areOptionalWeightsValid(Integer hardSkillsWeight, Integer softSkillsWeight, Integer experienceWeight) {
        boolean allNull = hardSkillsWeight == null && softSkillsWeight == null && experienceWeight == null;
        if (allNull) {
            return true;
        }

        boolean allPresent = hardSkillsWeight != null && softSkillsWeight != null && experienceWeight != null;
        if (!allPresent) {
            return false;
        }

        return hardSkillsWeight + softSkillsWeight + experienceWeight == 100;
    }
}
