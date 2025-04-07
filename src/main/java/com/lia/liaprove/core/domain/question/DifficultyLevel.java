package com.lia.liaprove.core.domain.question;

public enum DifficultyLevel {
    EASY("easy"), MEDIUM("medium"), HARD("hard");

    private String difficultyLevel;

    DifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }
    // This method may not be necessary
    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
}
