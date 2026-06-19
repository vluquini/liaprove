package com.lia.liaprove.core.domain.question;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MultipleChoiceQuestionTest {

    @Test
    void shouldExposeMultipleChoiceQuestionType() {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion();

        assertThat(question.getQuestionType()).isEqualTo(QuestionType.MULTIPLE_CHOICE);
    }

    @Test
    void shouldRejectNullAlternativesInConstructor() {
        assertThatThrownBy(() -> new MultipleChoiceQuestion(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Alternatives must not be null.");
    }

    @Test
    void shouldStoreImmutableCopyOfAlternativesFromConstructor() {
        Alternative first = new Alternative(UUID.randomUUID(), "Use an index.", true);
        List<Alternative> alternatives = new ArrayList<>();
        alternatives.add(first);

        MultipleChoiceQuestion question = new MultipleChoiceQuestion(alternatives);
        alternatives.add(new Alternative(UUID.randomUUID(), "Ignore indexes.", false));

        assertThat(question.getAlternatives()).containsExactly(first);
        assertThatThrownBy(() -> question.getAlternatives()
                .add(new Alternative(UUID.randomUUID(), "Add another.", false)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldRejectNullAlternativesWhenUpdating() {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(List.of());

        assertThatThrownBy(() -> question.updateAlternatives(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Alternatives must not be null.");
    }

    @Test
    void shouldReplaceAlternativesWithImmutableCopyWhenUpdating() {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(List.of(
                new Alternative(UUID.randomUUID(), "Old answer.", true)
        ));
        Alternative newAlternative = new Alternative(UUID.randomUUID(), "New answer.", true);
        List<Alternative> newAlternatives = new ArrayList<>();
        newAlternatives.add(newAlternative);

        question.updateAlternatives(newAlternatives);
        newAlternatives.add(new Alternative(UUID.randomUUID(), "Mutated answer.", false));

        assertThat(question.getAlternatives()).containsExactly(newAlternative);
        assertThatThrownBy(() -> question.getAlternatives()
                .add(new Alternative(UUID.randomUUID(), "Another answer.", false)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldAllowNullAlternativesThroughSetterForMapperCompatibility() {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(List.of());

        question.setAlternatives(null);

        assertThat(question.getAlternatives()).isNull();
    }

    @Test
    void shouldStoreImmutableCopyOfAlternativesThroughSetter() {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion();
        Alternative alternative = new Alternative(UUID.randomUUID(), "Use transactions.", true);
        List<Alternative> alternatives = new ArrayList<>();
        alternatives.add(alternative);

        question.setAlternatives(alternatives);
        alternatives.add(new Alternative(UUID.randomUUID(), "Skip transactions.", false));

        assertThat(question.getAlternatives()).containsExactly(alternative);
        assertThatThrownBy(() -> question.getAlternatives()
                .add(new Alternative(UUID.randomUUID(), "Another answer.", false)))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
