package com.lia.liaprove.core.domain.user;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserProfessionalTest {

    @Test
    void shouldNormalizeSkillsByTrimmingLowercasingRemovingBlankValuesAndDuplicates() {
        UserProfessional professional = new UserProfessional();

        professional.setHardSkills(List.of(" Java ", "java", " ", "Spring", "Spring"));
        professional.setSoftSkills(List.of(" Communication ", "", "Communication"));

        assertThat(professional.getHardSkills()).containsExactly("java", "spring");
        assertThat(professional.getSoftSkills()).containsExactly("communication");
    }

    @Test
    void shouldNormalizeNullSkillListsToEmptyLists() {
        UserProfessional professional = new UserProfessional();

        professional.setHardSkills(null);
        professional.setSoftSkills(null);

        assertThat(professional.getHardSkills()).isEmpty();
        assertThat(professional.getSoftSkills()).isEmpty();
    }

    @Test
    void shouldReplaceOnlyProvidedSkillLists() {
        UserProfessional professional = new UserProfessional();
        professional.setHardSkills(List.of("Java"));
        professional.setSoftSkills(List.of("Communication"));

        professional.updateSkills(null, List.of(" Leadership ", "Leadership"));

        assertThat(professional.getHardSkills()).containsExactly("java");
        assertThat(professional.getSoftSkills()).containsExactly("leadership");
    }

}
