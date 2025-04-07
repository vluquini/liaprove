package com.lia.liaprove.core.domain.user;

import com.lia.liaprove.core.domain.assessment.Certificate;

import java.util.List;

public class UserProfessional extends User{
    private Integer totalAssessmentsTaken;
    private List<Certificate> certificates;
}
