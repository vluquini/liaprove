package com.lia.liaprove.core.algorithms.genetic;

import java.util.Objects;
import java.util.UUID;

/**
 * Representação simples de indivíduo (aqui, cada indivíduo corresponde a um Recruiter).
 */
public class Individual {
    private final UUID recruiterId;
    private double gene;   // representação 1D: gene único = weightNormalized (0..1)
    private double fitness;

    public Individual(UUID recruiterId, double gene) {
        this.recruiterId = Objects.requireNonNull(recruiterId, "recruiterId");
        setGene(gene);
    }

    public UUID getRecruiterId() { return recruiterId; }
    public double getGene() { return gene; }
    public void setGene(double gene) {
        if (!Double.isFinite(gene) || gene < 0.0 || gene > 1.0) {
            throw new IllegalArgumentException("gene must be finite and in [0,1]");
        }
        this.gene = gene;
    }
    public double getFitness() { return fitness; }
    public void setFitness(double fitness) {
        if (!Double.isFinite(fitness) || fitness < 0.0 || fitness > 1.0) {
            throw new IllegalArgumentException("fitness must be finite and in [0,1]");
        }
        this.fitness = fitness;
    }
}
