package com.lia.liaprove.core.domain.user;

import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.domain.assessment.Assessment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Representa um usuário do tipo Profissional de TI.
 * Esta classe modela os profissionais que podem realizar avaliações,
 * submeter questões e interagir com a plataforma.
 */
public class UserProfessional extends User{
    private List<String> hardSkills = new ArrayList<>();
    private List<String> softSkills = new ArrayList<>();

    public UserProfessional() {}

    public UserProfessional(UUID id, String name, String email, String password, String occupation, String bio,
                            ExperienceLevel experienceLevel, UserRole role, Integer voteWeight, Integer totalAssessmentsTaken,
                            List<Certificate> certificates, Float averageScore, LocalDateTime registrationDate,
                            LocalDateTime lastLogin) {
        super(id, name, email, password, occupation, bio, experienceLevel, role, voteWeight, totalAssessmentsTaken,
                certificates, averageScore, registrationDate, lastLogin);
    }

    public List<String> getHardSkills() {
        return hardSkills;
    }

    public void setHardSkills(List<String> hardSkills) {
        this.hardSkills = normalizeSkills(hardSkills);
    }

    public List<String> getSoftSkills() {
        return softSkills;
    }

    public void setSoftSkills(List<String> softSkills) {
        this.softSkills = normalizeSkills(softSkills);
    }

    // ---------- Métodos de domínio  ----------

    /**
     * Wrapper semântico para registrar resultado de avaliação.
     * Delegação direta para o comportamento genérico do agregado.
     */
    public void recordAssessmentResultAsProfessional(float score) {
        // validação leve
        if (score < 0.0f) {
            throw new IllegalArgumentException("score must be >= 0");
        }
        this.recordAssessmentResult(score);
    }

    /**
     * Atualiza dados de perfil do profissional.
     */
    public void updateProfile(String occupation, String bio) {
        if (occupation != null) {
            this.setOccupation(occupation.trim());
        }
        if (bio != null) {
            this.setBio(bio.trim());
        }
    }

    public void updateSkills(List<String> hardSkills, List<String> softSkills) {
        if (hardSkills != null) {
            this.hardSkills = normalizeSkills(hardSkills);
        }
        if (softSkills != null) {
            this.softSkills = normalizeSkills(softSkills);
        }
    }

    /**
     * Verificação simples se o usuário é elegível a tentar uma avaliação.
     * NÃO substitui regras de negócio completas (por ex., limites de retake),
     * que devem existir no AssessmentService/usecase.
     */
    public boolean isEligibleForAssessment(Assessment assessment) {
        return assessment != null && assessment.canBeAttemptedBy(this);
    }

    private List<String> normalizeSkills(List<String> skills) {
        if (skills == null) {
            return new ArrayList<>();
        }
        return skills.stream()
                .filter(skill -> skill != null && !skill.isBlank())
                .map(String::trim)
                .map(skill -> skill.toLowerCase(Locale.ROOT))
                .distinct()
                .toList();
    }
}
