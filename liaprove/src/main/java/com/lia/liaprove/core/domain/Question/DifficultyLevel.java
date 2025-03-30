package com.lia.liaprove.core.domain.Question;

public enum DifficultyLevel {
    Easy("easy"), Medium("medium"), Hard("hard");

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
