package com.lia.liaprove.core.domain.user;

import com.lia.liaprove.core.domain.assessment.Certificate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserProfessional extends User{

    public UserProfessional(UUID id, String name, String email, String password, String occupation, String bio, ExperienceLevel experienceLevel, UserRole role, Integer voteWeight, Integer totalAssessmentsTaken, List<Certificate> certificates, Float averageScore, LocalDateTime registrationDate, LocalDateTime lastLogin) {
        super(id, name, email, password, occupation, bio, experienceLevel, role, voteWeight, totalAssessmentsTaken, certificates, averageScore, registrationDate, lastLogin);
    }
}
