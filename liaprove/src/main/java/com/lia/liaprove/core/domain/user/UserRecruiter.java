package com.lia.liaprove.core.domain.user;

import com.lia.liaprove.core.domain.assessment.Certificate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserRecruiter extends User {
    private String companyName;
    private String companyEmail;
    private Integer totalAssessmentsCreated;
    // Pode ser usado pela comunidade para avaliar os Recruiters: questões publicadas, feedbacks, etc.
    private Float recruiterRating;
    // Contador para cálculo incremental da média do recruiterRating
    private Integer recruiterRatingCount = 0;

    public UserRecruiter() {}

    public UserRecruiter(UUID id, String name, String email, String password, String occupation, String bio, ExperienceLevel experienceLevel, UserRole role, Integer voteWeight, Integer totalAssessmentsTaken, List<Certificate> certificates, Float averageScore, LocalDateTime registrationDate, LocalDateTime lastLogin) {
        super(id, name, email, password, occupation, bio, experienceLevel, role, voteWeight, totalAssessmentsTaken, certificates, averageScore, registrationDate, lastLogin);
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public Integer getTotalAssessmentsCreated() {
        return totalAssessmentsCreated;
    }

    public void setTotalAssessmentsCreated(Integer totalAssessmentsCreated) {
        this.totalAssessmentsCreated = totalAssessmentsCreated;
    }

    public Float getRecruiterRating() {
        return recruiterRating;
    }

    public void setRecruiterRating(Float recruiterRating) {
        this.recruiterRating = recruiterRating;
    }

    public Integer getRecruiterRatingCount() {
        return recruiterRatingCount;
    }

    public void setRecruiterRatingCount(Integer recruiterRatingCount) {
        this.recruiterRatingCount = recruiterRatingCount;
    }

    // ---------- Métodos de domínio  ----------

    /**
     * Incrementa o contador de avaliações criadas por este recruiter.
     * Deve ser chamado pelo Use Case que cria a avaliação após persistir com sucesso.
     */
    public void incrementAssessmentsCreated() {
        if (this.totalAssessmentsCreated == null) {
            this.totalAssessmentsCreated = 1;
        } else {
            this.totalAssessmentsCreated = this.totalAssessmentsCreated + 1;
        }
    }

    /**
     * Atualiza a média da avaliação do recrutador de forma incremental.
     * newRating deve estar no intervalo esperado (ex.: 0..5).
     */
    public void updateRecruiterRating(float newRating) {
        if (newRating < 0f) {
            throw new IllegalArgumentException("newRating must be >= 0");
        }
        int count = (this.recruiterRatingCount == null) ? 0 : this.recruiterRatingCount;
        float oldAvg = (this.recruiterRating == null) ? 0f : this.recruiterRating;
        float newAvg = (oldAvg * count + newRating) / (count + 1);
        this.recruiterRating = newAvg;
        this.recruiterRatingCount = count + 1;
    }

    /**
     * Uma métrica simples de engajamento que combina número de avaliações criadas
     * e rating recebido — útil como input para algoritmos (GA).
     *
     * Fórmula deliberadamente simples e explicável:
     * engagement = alpha * normalizedAssessmentsCreated + beta * normalizedRating
     * Onde normalização é feita por um divisor/config externo (aqui usamos constantes simples).
     */
    public double getRecruiterEngagementScore() {
        final double ALPHA = 0.6;
        final double BETA  = 0.4;
        final double MAX_EXPECTED_ASSESSMENTS = 100.0;  // parâmetro simples, ajustável
        final double MAX_RATING = 5.0;                  // se rating for 0..5

        double normalizedAssessments = Math.min(1.0, (this.totalAssessmentsCreated == null ? 0 : this.totalAssessmentsCreated) / MAX_EXPECTED_ASSESSMENTS);
        double normalizedRating = Math.min(1.0, (this.recruiterRating == null ? 0 : this.recruiterRating) / MAX_RATING);

        return ALPHA * normalizedAssessments + BETA * normalizedRating;
    }

    /**
     * Checagem local de invariantes para criação de avaliação.
     * Não substitui autorização/permissão do sistema — isto será feito em camada superior.
     */
    public boolean canCreateAssessment() {
        // Exemplo simples: apenas checa se recruiter tem email corporativo válido
        return this.companyEmail != null && !this.companyEmail.isBlank();
    }

    /**
     * Atualiza dados da empresa com validação mínima.
     */
    public void updateCompanyInfo(String companyName, String companyEmail) {
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("companyName must not be null/blank");
        }
        if (companyEmail == null || companyEmail.isBlank()) {
            throw new IllegalArgumentException("companyEmail must not be null/blank");
        }
        // validação simples de email
        if (!companyEmail.contains("@")) {
            throw new IllegalArgumentException("companyEmail seems invalid");
        }
        this.companyName = companyName.trim();
        this.companyEmail = companyEmail.trim().toLowerCase();
    }
}
