package com.lia.liaprove.core.algorithms.genetic;

import java.util.UUID;

/**
 * Representação simples de indivíduo (aqui, cada indivíduo corresponde a um Recruiter).
 */
public class Individual {
    private final UUID recruiterId;
    private double gene;   // representação 1D: gene único = weightNormalized (0..1)
    private double fitness;

    public Individual(UUID recruiterId, double gene) {
        this.recruiterId = recruiterId;
        this.gene = gene;
    }

    public UUID getRecruiterId() { return recruiterId; }
    public double getGene() { return gene; }
    public void setGene(double gene) { this.gene = gene; }
    public double getFitness() { return fitness; }
    public void setFitness(double fitness) { this.fitness = fitness; }
}
