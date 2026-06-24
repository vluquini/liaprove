package com.lia.liaprove.core.domain.user;

import com.lia.liaprove.core.domain.assessment.Certificate;

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

    // ---------- Métodos de domínio ----------

    public void updateSkills(List<String> hardSkills, List<String> softSkills) {
        if (hardSkills != null) {
            this.hardSkills = normalizeSkills(hardSkills);
        }
        if (softSkills != null) {
            this.softSkills = normalizeSkills(softSkills);
        }
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
