package com.lia.liaprove.core.domain.assessment;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AnswerTest {

    @Test
    void shouldStoreQuestionAnswerPayloadInAnswer() {
        UUID questionId = UUID.randomUUID();
        UUID alternativeId = UUID.randomUUID();

        Answer answer = new Answer(questionId);
        answer.setSelectedAlternativeId(alternativeId);
        answer.setProjectUrl("https://github.com/acme/project");
        answer.setTextResponse("plain text response");

        assertThat(answer.getQuestionId()).isEqualTo(questionId);
        assertThat(answer.getSelectedAlternativeId()).isEqualTo(alternativeId);
        assertThat(answer.getProjectUrl()).isEqualTo("https://github.com/acme/project");
        assertThat(answer.getTextResponse()).isEqualTo("plain text response");
    }

    @Test
    void shouldCreateMultipleChoiceAnswerUsingFactory() {
        UUID questionId = UUID.randomUUID();
        UUID alternativeId = UUID.randomUUID();

        Answer answer = Answer.multipleChoice(questionId, alternativeId);

        assertThat(answer.getQuestionId()).isEqualTo(questionId);
        assertThat(answer.getSelectedAlternativeId()).isEqualTo(alternativeId);
        assertThat(answer.getProjectUrl()).isNull();
        assertThat(answer.getTextResponse()).isNull();
        assertThat(answer.hasSelectedAlternative()).isTrue();
        assertThat(answer.hasManualPayload()).isFalse();
    }

    @Test
    void shouldCreateProjectAnswerUsingFactory() {
        UUID questionId = UUID.randomUUID();

        Answer answer = Answer.project(questionId, "https://github.com/acme/project");

        assertThat(answer.getQuestionId()).isEqualTo(questionId);
        assertThat(answer.getSelectedAlternativeId()).isNull();
        assertThat(answer.getProjectUrl()).isEqualTo("https://github.com/acme/project");
        assertThat(answer.getTextResponse()).isNull();
        assertThat(answer.hasSelectedAlternative()).isFalse();
        assertThat(answer.hasManualPayload()).isTrue();
    }

    @Test
    void shouldCreateOpenTextAnswerUsingFactory() {
        UUID questionId = UUID.randomUUID();

        Answer answer = Answer.openText(questionId, "plain text response");

        assertThat(answer.getQuestionId()).isEqualTo(questionId);
        assertThat(answer.getSelectedAlternativeId()).isNull();
        assertThat(answer.getProjectUrl()).isNull();
        assertThat(answer.getTextResponse()).isEqualTo("plain text response");
        assertThat(answer.hasSelectedAlternative()).isFalse();
        assertThat(answer.hasManualPayload()).isTrue();
    }

    @Test
    void shouldRejectNullQuestionId() {
        assertThatThrownBy(() -> new Answer(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("questionId must not be null");
    }
}
