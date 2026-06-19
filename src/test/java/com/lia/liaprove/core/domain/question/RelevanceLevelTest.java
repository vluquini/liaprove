package com.lia.liaprove.core.domain.question;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RelevanceLevelTest {

    @Test
    void shouldConvertValidNumericValuesToRelevanceLevel() {
        assertThat(RelevanceLevel.of(1)).isEqualTo(RelevanceLevel.ONE);
        assertThat(RelevanceLevel.of(2)).isEqualTo(RelevanceLevel.TWO);
        assertThat(RelevanceLevel.of(3)).isEqualTo(RelevanceLevel.THREE);
        assertThat(RelevanceLevel.of(4)).isEqualTo(RelevanceLevel.FOUR);
        assertThat(RelevanceLevel.of(5)).isEqualTo(RelevanceLevel.FIVE);
    }

    @Test
    void shouldExposeNumericRelevanceLevel() {
        assertThat(RelevanceLevel.ONE.getRelevanceLevel()).isEqualTo(1);
        assertThat(RelevanceLevel.THREE.getRelevanceLevel()).isEqualTo(3);
        assertThat(RelevanceLevel.FIVE.getRelevanceLevel()).isEqualTo(5);
    }

    @Test
    void shouldRejectInvalidNumericValues() {
        assertThatThrownBy(() -> RelevanceLevel.of(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Valor inválido: 0");
        assertThatThrownBy(() -> RelevanceLevel.of(6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Valor inválido: 6");
    }
}
