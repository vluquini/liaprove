package com.lia.liaprove.core.domain.assessment;

import com.lia.liaprove.core.domain.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

/*
Esta entidade representa uma avaliação feita por um Usuário.
Isto pois: um SystemAssessment pode ser feita por apenas um User
e uma PersonalizedAssessment pode ser feita por N Users.
 */
public class AssessmentAttempt {
    private UUID id;
    private Assessment assessment;
    private User user;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Integer accuracyRate;
}
