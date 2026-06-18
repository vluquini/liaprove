package com.lia.liaprove.core.domain.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExperienceLevelTest {

    @Test
    void shouldExposeNumericLevelAndLowercaseLabel() {
        assertThat(ExperienceLevel.JUNIOR.getLevel()).isEqualTo(1);
        assertThat(ExperienceLevel.PLENO.getLevel()).isEqualTo(2);
        assertThat(ExperienceLevel.SENIOR.getLevel()).isEqualTo(3);
        assertThat(ExperienceLevel.SENIOR.getLabel()).isEqualTo("senior");
        assertThat(ExperienceLevel.SENIOR).hasToString("senior");
    }

    @Test
    void shouldParseExperienceLevelFromLabelOrEnumNameIgnoringCaseAndWhitespace() {
        assertThat(ExperienceLevel.fromString(" junior ")).isEqualTo(ExperienceLevel.JUNIOR);
        assertThat(ExperienceLevel.fromString("PLENO")).isEqualTo(ExperienceLevel.PLENO);
        assertThat(ExperienceLevel.fromString("Senior")).isEqualTo(ExperienceLevel.SENIOR);
    }

    @Test
    void shouldRejectNullOrUnknownExperienceLevel() {
        assertThatThrownBy(() -> ExperienceLevel.fromString(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must not be null");
        assertThatThrownBy(() -> ExperienceLevel.fromString("principal"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown ExperienceLevel: principal");
    }

    @Test
    void shouldReturnFallbackWhenParsingFails() {
        assertThat(ExperienceLevel.fromStringOrDefault("unknown", ExperienceLevel.JUNIOR))
                .isEqualTo(ExperienceLevel.JUNIOR);
    }
}
