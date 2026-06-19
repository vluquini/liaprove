package com.lia.liaprove.core.domain.question;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AlternativeTest {

    @Test
    void shouldExposeAlternativeRecordValues() {
        UUID id = UUID.randomUUID();

        Alternative alternative = new Alternative(id, "Use a B-tree index.", true);

        assertThat(alternative.id()).isEqualTo(id);
        assertThat(alternative.text()).isEqualTo("Use a B-tree index.");
        assertThat(alternative.correct()).isTrue();
    }
}
