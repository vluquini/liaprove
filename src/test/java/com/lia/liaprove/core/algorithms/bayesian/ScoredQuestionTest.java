package com.lia.liaprove.core.algorithms.bayesian;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScoredQuestionTest {

    @Test
    void shouldExposeQuestionAndScore() {
        Question question = question();

        ScoredQuestion scoredQuestion = new ScoredQuestion(question, 0.75);

        assertThat(scoredQuestion.getQuestion()).isSameAs(question);
        assertThat(scoredQuestion.getScore()).isEqualTo(0.75);
    }

    @Test
    void shouldRejectNullQuestion() {
        assertThatThrownBy(() -> new ScoredQuestion(null, 0.5))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("question must not be null");
    }

    @Test
    void shouldRejectScoreOutsideProbabilityRange() {
        Question question = question();

        assertThatThrownBy(() -> new ScoredQuestion(question, -0.01))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("score must be finite and between 0.0 and 1.0");
        assertThatThrownBy(() -> new ScoredQuestion(question, 1.01))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("score must be finite and between 0.0 and 1.0");
    }

    @Test
    void shouldRejectNonFiniteScore() {
        assertThatThrownBy(() -> new ScoredQuestion(question(), Double.POSITIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("score must be finite and between 0.0 and 1.0");
    }

    private static Question question() {
        return new ProjectQuestion(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Project question",
                "Build something useful",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.MEDIUM,
                RelevanceLevel.FOUR,
                LocalDateTime.of(2026, 1, 1, 9, 0),
                LocalDateTime.of(2026, 1, 8, 9, 0),
                QuestionStatus.FINISHED,
                RelevanceLevel.THREE,
                0
        );
    }
}
