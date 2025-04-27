package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.user.UserRecruiter;

import java.time.LocalDateTime;

/*
Esta entidade representa Assessments personalizados
criados por um Recruiter
 */
public class PersonalizedAssessment extends Assessment {
    private UserRecruiter createdBy;
    // Data de expiração da avaliação
    private LocalDateTime expirationDate;
    // Total de vezes que a avaliação foi realizada
    private int totalAttempts;
    // Status da avaliação
    private AssessmentStatus status;
}
