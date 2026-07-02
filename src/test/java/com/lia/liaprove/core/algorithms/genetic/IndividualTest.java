package com.lia.liaprove.core.algorithms.genetic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IndividualTest {

    @Test
    @DisplayName("Should create individual with valid recruiter and normalized gene")
    void shouldCreateValidIndividual() {
        UUID recruiterId = UUID.randomUUID();

        Individual individual = new Individual(recruiterId, 0.75);

        assertThat(individual.getRecruiterId()).isEqualTo(recruiterId);
        assertThat(individual.getGene()).isEqualTo(0.75);
        assertThat(individual.getFitness()).isZero();
    }

    @Test
    @DisplayName("Should reject null recruiter id")
    void shouldRejectNullRecruiterId() {
        assertThatThrownBy(() -> new Individual(null, 0.5))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("recruiterId");
    }

    @Test
    @DisplayName("Should reject invalid gene and fitness values")
    void shouldRejectInvalidGeneAndFitness() {
        Individual individual = new Individual(UUID.randomUUID(), 0.5);

        assertThatThrownBy(() -> individual.setGene(Double.NaN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("gene must be finite and in [0,1]");

        assertThatThrownBy(() -> individual.setFitness(1.1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("fitness must be finite and in [0,1]");
    }
}
