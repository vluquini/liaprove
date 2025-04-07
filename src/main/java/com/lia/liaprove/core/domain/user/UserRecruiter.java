package com.lia.liaprove.core.domain.user;

public class UserRecruiter extends User {
    private String companyName;
    private String companyEmail;
    private Integer totalAssessmentsCreated;
    // Pode ser usado pela comunidade para avaliar os Recruiters: questões publicadas, feedbacks, etc.
    private Float recruiterRating;

}
