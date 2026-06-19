package com.lia.liaprove.core.domain.question;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DifficultyLevelTest {

    @Test
    void shouldExposeDifficultyLevelValue() {
        assertThat(DifficultyLevel.EASY.getDifficultyLevel()).isEqualTo("easy");
        assertThat(DifficultyLevel.MEDIUM.getDifficultyLevel()).isEqualTo("medium");
        assertThat(DifficultyLevel.HARD.getDifficultyLevel()).isEqualTo("hard");
    }
}
