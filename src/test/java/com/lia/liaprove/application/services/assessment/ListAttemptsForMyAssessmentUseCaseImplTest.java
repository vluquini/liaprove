package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAttemptsForMyAssessmentUseCaseImplTest {

    @Mock
    private AssessmentGateway assessmentGateway;

    @Mock
    private AssessmentAttemptGateway attemptGateway;

    @Mock
    private UserGateway userGateway;

    @Test
    void shouldAllowRecruiterToListAttemptsForOwnAssessment() {
        UUID recruiterId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UserRecruiter recruiter = recruiter(recruiterId);
        PersonalizedAssessment assessment = assessment(assessmentId, recruiter);
        AssessmentAttempt attempt = attempt(assessment);

        when(assessmentGateway.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(userGateway.findById(recruiterId)).thenReturn(Optional.of(recruiter));
        when(attemptGateway.findSummariesByAssessmentId(assessmentId)).thenReturn(List.of(attempt));

        ListAttemptsForMyAssessmentUseCaseImpl useCase =
                new ListAttemptsForMyAssessmentUseCaseImpl(assessmentGateway, attemptGateway, userGateway);

        List<AssessmentAttempt> result = useCase.execute(assessmentId, recruiterId);

        assertThat(result).containsExactly(attempt);
        verify(attemptGateway).findSummariesByAssessmentId(assessmentId);
    }

    @Test
    void shouldAllowAdminToListAttemptsForAnyPersonalizedAssessment() {
        UUID adminId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UserProfessional admin = admin(adminId);
        PersonalizedAssessment assessment = assessment(assessmentId, recruiter(UUID.randomUUID()));
        AssessmentAttempt attempt = attempt(assessment);

        when(assessmentGateway.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(userGateway.findById(adminId)).thenReturn(Optional.of(admin));
        when(attemptGateway.findSummariesByAssessmentId(assessmentId)).thenReturn(List.of(attempt));

        ListAttemptsForMyAssessmentUseCaseImpl useCase =
                new ListAttemptsForMyAssessmentUseCaseImpl(assessmentGateway, attemptGateway, userGateway);

        List<AssessmentAttempt> result = useCase.execute(assessmentId, adminId);

        assertThat(result).containsExactly(attempt);
        verify(attemptGateway).findSummariesByAssessmentId(assessmentId);
    }

    @Test
    void shouldRejectRecruiterListingAttemptsForAnotherRecruiterAssessment() {
        UUID requesterId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UserRecruiter requester = recruiter(requesterId);
        PersonalizedAssessment assessment = assessment(assessmentId, recruiter(UUID.randomUUID()));

        when(assessmentGateway.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(userGateway.findById(requesterId)).thenReturn(Optional.of(requester));

        ListAttemptsForMyAssessmentUseCaseImpl useCase =
                new ListAttemptsForMyAssessmentUseCaseImpl(assessmentGateway, attemptGateway, userGateway);

        assertThatThrownBy(() -> useCase.execute(assessmentId, requesterId))
                .isInstanceOf(AuthorizationException.class);

        verify(attemptGateway, never()).findSummariesByAssessmentId(assessmentId);
    }

    private AssessmentAttempt attempt(PersonalizedAssessment assessment) {
        return new AssessmentAttempt(
                UUID.randomUUID(),
                assessment,
                null,
                List.of(),
                List.of(),
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                80,
                null,
                AssessmentAttemptStatus.COMPLETED
        );
    }

    private PersonalizedAssessment assessment(UUID assessmentId, UserRecruiter recruiter) {
        return new PersonalizedAssessment(
                assessmentId,
                "Assessment",
                "Description",
                LocalDateTime.now(),
                List.of(),
                Duration.ofMinutes(60),
                recruiter,
                LocalDateTime.now().plusDays(7),
                3,
                UUID.randomUUID().toString(),
                PersonalizedAssessmentStatus.ACTIVE,
                AssessmentCriteriaWeights.defaultWeights()
        );
    }

    private UserRecruiter recruiter(UUID recruiterId) {
        UserRecruiter recruiter = new UserRecruiter(
                recruiterId,
                "Recruiter",
                "recruiter@example.com",
                "hashed-password",
                "Recruiter",
                "Bio",
                ExperienceLevel.SENIOR,
                UserRole.RECRUITER,
                5,
                0,
                List.of(),
                0.0f,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        recruiter.setCompanyEmail("recruiter@example.com");
        recruiter.setCompanyName("Acme");
        return recruiter;
    }

    private UserProfessional admin(UUID adminId) {
        return new UserProfessional(
                adminId,
                "Admin",
                "admin@example.com",
                "hashed-password",
                "Admin",
                "Bio",
                ExperienceLevel.SENIOR,
                UserRole.ADMIN,
                1,
                0,
                List.of(),
                0.0f,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
