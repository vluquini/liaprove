package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.question.Question;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssessmentTest {

    @Test
    void shouldRejectBlankTitle() {
        assertThatThrownBy(() -> assessment(" ", "Description", LocalDateTime.now(), List.of(), Duration.ofMinutes(10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("title must not be blank");
    }

    @Test
    void shouldRejectBlankDescription() {
        assertThatThrownBy(() -> assessment("Title", " ", LocalDateTime.now(), List.of(), Duration.ofMinutes(10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("description must not be blank");
    }

    @Test
    void shouldRejectNullCreationDate() {
        assertThatThrownBy(() -> assessment("Title", "Description", null, List.of(), Duration.ofMinutes(10)))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("creationDate must not be null");
    }

    @Test
    void shouldRejectNullQuestions() {
        assertThatThrownBy(() -> assessment("Title", "Description", LocalDateTime.now(), null, Duration.ofMinutes(10)))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("questions must not be null");
    }

    @Test
    void shouldRejectNullEvaluationTimer() {
        assertThatThrownBy(() -> assessment("Title", "Description", LocalDateTime.now(), List.of(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("evaluationTimer must not be null");
    }

    private void assessment(
            String title,
            String description,
            LocalDateTime creationDate,
            List<Question> questions,
            Duration evaluationTimer
    ) {
        new SystemAssessment(
                UUID.randomUUID(),
                title,
                description,
                creationDate,
                questions,
                evaluationTimer
        );
    }
}
