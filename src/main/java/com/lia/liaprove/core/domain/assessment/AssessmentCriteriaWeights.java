package com.lia.liaprove.core.domain.assessment;

import java.util.Objects;

public class AssessmentCriteriaWeights {

    private static final int TOTAL_WEIGHT = 100;
    private static final int DEFAULT_HARD_SKILLS_WEIGHT = 34;
    private static final int DEFAULT_SOFT_SKILLS_WEIGHT = 33;
    private static final int DEFAULT_EXPERIENCE_WEIGHT = 33;

    private int hardSkillsWeight;
    private int softSkillsWeight;
    private int experienceWeight;

    public AssessmentCriteriaWeights() {
        this(DEFAULT_HARD_SKILLS_WEIGHT, DEFAULT_SOFT_SKILLS_WEIGHT, DEFAULT_EXPERIENCE_WEIGHT);
    }

    public AssessmentCriteriaWeights(int hardSkillsWeight, int softSkillsWeight, int experienceWeight) {
        validate(hardSkillsWeight, softSkillsWeight, experienceWeight);
        this.hardSkillsWeight = hardSkillsWeight;
        this.softSkillsWeight = softSkillsWeight;
        this.experienceWeight = experienceWeight;
    }

    public static AssessmentCriteriaWeights defaultWeights() {
        return new AssessmentCriteriaWeights();
    }

    public int getHardSkillsWeight() {
        return hardSkillsWeight;
    }

    public void setHardSkillsWeight(int hardSkillsWeight) {
        validate(hardSkillsWeight, this.softSkillsWeight, this.experienceWeight);
        this.hardSkillsWeight = hardSkillsWeight;
    }

    public int getSoftSkillsWeight() {
        return softSkillsWeight;
    }

    public void setSoftSkillsWeight(int softSkillsWeight) {
        validate(this.hardSkillsWeight, softSkillsWeight, this.experienceWeight);
        this.softSkillsWeight = softSkillsWeight;
    }

    public int getExperienceWeight() {
        return experienceWeight;
    }

    public void setExperienceWeight(int experienceWeight) {
        validate(this.hardSkillsWeight, this.softSkillsWeight, experienceWeight);
        this.experienceWeight = experienceWeight;
    }

    private static void validate(int hardSkillsWeight, int softSkillsWeight, int experienceWeight) {
        if (hardSkillsWeight < 0 || softSkillsWeight < 0 || experienceWeight < 0) {
            throw new IllegalArgumentException("Criteria weights must be non-negative.");
        }

        if (hardSkillsWeight + softSkillsWeight + experienceWeight != TOTAL_WEIGHT) {
            throw new IllegalArgumentException("Criteria weights must sum to 100.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssessmentCriteriaWeights that)) return false;
        return hardSkillsWeight == that.hardSkillsWeight
                && softSkillsWeight == that.softSkillsWeight
                && experienceWeight == that.experienceWeight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hardSkillsWeight, softSkillsWeight, experienceWeight);
    }
}
