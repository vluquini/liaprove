package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.exceptions.assessment.AssessmentNotFoundException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPersonalizedAssessmentUseCaseImplTest {

    @Mock
    private AssessmentGateway assessmentGateway;

    @Mock
    private UserGateway userGateway;

    @Test
    void shouldReturnRecruiterOwnAssessment() {
        UUID recruiterId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UserRecruiter recruiter = recruiter(recruiterId);
        PersonalizedAssessment assessment = assessment(assessmentId, recruiter);

        when(userGateway.findById(recruiterId)).thenReturn(Optional.of(recruiter));
        when(assessmentGateway.findById(assessmentId)).thenReturn(Optional.of(assessment));

        GetPersonalizedAssessmentUseCaseImpl useCase =
                new GetPersonalizedAssessmentUseCaseImpl(assessmentGateway, userGateway);

        assertThat(useCase.execute(assessmentId, recruiterId)).isEqualTo(assessment);
    }

    @Test
    void shouldAllowAdminToReturnAnyPersonalizedAssessment() {
        UUID adminId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UserProfessional admin = admin(adminId);
        PersonalizedAssessment assessment = assessment(assessmentId, recruiter(UUID.randomUUID()));

        when(userGateway.findById(adminId)).thenReturn(Optional.of(admin));
        when(assessmentGateway.findById(assessmentId)).thenReturn(Optional.of(assessment));

        GetPersonalizedAssessmentUseCaseImpl useCase =
                new GetPersonalizedAssessmentUseCaseImpl(assessmentGateway, userGateway);

        assertThat(useCase.execute(assessmentId, adminId)).isEqualTo(assessment);
    }

    @Test
    void shouldRejectRecruiterAccessingAnotherRecruiterAssessment() {
        UUID requesterId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        UserRecruiter requester = recruiter(requesterId);
        PersonalizedAssessment assessment = assessment(assessmentId, recruiter(UUID.randomUUID()));

        when(userGateway.findById(requesterId)).thenReturn(Optional.of(requester));
        when(assessmentGateway.findById(assessmentId)).thenReturn(Optional.of(assessment));

        GetPersonalizedAssessmentUseCaseImpl useCase =
                new GetPersonalizedAssessmentUseCaseImpl(assessmentGateway, userGateway);

        assertThatThrownBy(() -> useCase.execute(assessmentId, requesterId))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void shouldThrowWhenAssessmentDoesNotExist() {
        UUID requesterId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();

        when(userGateway.findById(requesterId)).thenReturn(Optional.of(recruiter(requesterId)));
        when(assessmentGateway.findById(assessmentId)).thenReturn(Optional.empty());

        GetPersonalizedAssessmentUseCaseImpl useCase =
                new GetPersonalizedAssessmentUseCaseImpl(assessmentGateway, userGateway);

        assertThatThrownBy(() -> useCase.execute(assessmentId, requesterId))
                .isInstanceOf(AssessmentNotFoundException.class);
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
        UserProfessional admin = professional(adminId);
        admin.setRole(UserRole.ADMIN);
        return admin;
    }

    private UserProfessional professional(UUID professionalId) {
        return new UserProfessional(
                professionalId,
                "Professional",
                "professional@example.com",
                "hashed-password",
                "Developer",
                "Bio",
                ExperienceLevel.SENIOR,
                UserRole.PROFESSIONAL,
                1,
                0,
                List.of(),
                0.0f,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
