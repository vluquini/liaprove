package com.lia.liaprove.core.domain.question;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeAreaTest {

    @Test
    void shouldExposeDisplayNames() {
        assertThat(KnowledgeArea.SOFTWARE_DEVELOPMENT.getDisplayName()).isEqualTo("Software Development");
        assertThat(KnowledgeArea.DATABASE.getDisplayName()).isEqualTo("Database");
        assertThat(KnowledgeArea.CYBERSECURITY.getDisplayName()).isEqualTo("Cybersecurity");
        assertThat(KnowledgeArea.NETWORKS.getDisplayName()).isEqualTo("Networks");
        assertThat(KnowledgeArea.AI.getDisplayName()).isEqualTo("Artificial Intelligence");
    }
}
