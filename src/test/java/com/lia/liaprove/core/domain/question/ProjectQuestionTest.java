package com.lia.liaprove.core.domain.question;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectQuestionTest {

    @Test
    void shouldExposeProjectQuestionType() {
        ProjectQuestion question = new ProjectQuestion();

        assertThat(question.getQuestionType()).isEqualTo(QuestionType.PROJECT);
    }
}
