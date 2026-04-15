package com.lia.liaprove.core.domain.assessment;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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
}
