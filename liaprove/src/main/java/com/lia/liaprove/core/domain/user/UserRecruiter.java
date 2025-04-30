package com.lia.liaprove.core.domain.user;

import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.domain.metrics.Feedback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserRecruiter extends User {
    private String companyName;
    private String companyEmail;
    private Integer totalAssessmentsCreated;
    // Pode ser usado pela comunidade para avaliar os Recruiters: questões publicadas, feedbacks, etc.
    private Float recruiterRating;

    public UserRecruiter(UUID id, String name, String email, String password, String occupation, String bio, ExperienceLevel experienceLevel, UserRole role, Integer voteWeight, Integer totalAssessmentsTaken, List<Certificate> certificates, List<Feedback> feedbacks, Float averageScore, LocalDateTime registrationDate, LocalDateTime lastLogin) {
        super(id, name, email, password, occupation, bio, experienceLevel, role, voteWeight, totalAssessmentsTaken, certificates, feedbacks, averageScore, registrationDate, lastLogin);
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
}
