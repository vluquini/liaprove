package com.lia.liaprove.core.domain.user;

public enum ExperienceLevel {
    JUNIOR("junior"), PLENO("pleno"), SENIOR("senior");

    private String experienceLevel;

    ExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }
}
