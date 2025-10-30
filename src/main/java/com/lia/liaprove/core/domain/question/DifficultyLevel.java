package com.lia.liaprove.core.domain.question;

/**
 * Enumeração que representa os níveis de dificuldade de uma questão.
 */
public enum DifficultyLevel {
    EASY("easy"), MEDIUM("medium"), HARD("hard");

    private String difficultyLevel;

    DifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

}
