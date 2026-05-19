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
import com.lia.liaprove.core.domain.user.UserStatus;
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
class ListPersonalizedAssessmentsUseCaseImplTest {

    @Mock
    private AssessmentGateway assessmentGateway;

    @Mock
    private UserGateway userGateway;

    @Test
    void shouldListOnlyRecruiterAssessmentsForRecruiter() {
        UUID requesterId = UUID.randomUUID();
        UserRecruiter recruiter = recruiter(requesterId);
        PersonalizedAssessment assessment = assessment(UUID.randomUUID(), recruiter);

        when(userGateway.findById(requesterId)).thenReturn(Optional.of(recruiter));
        when(assessmentGateway.findPersonalizedAssessmentsByCreatorId(requesterId)).thenReturn(List.of(assessment));

        ListPersonalizedAssessmentsUseCaseImpl useCase =
                new ListPersonalizedAssessmentsUseCaseImpl(assessmentGateway, userGateway);

        List<PersonalizedAssessment> result = useCase.execute(requesterId);

        assertThat(result).containsExactly(assessment);
        verify(assessmentGateway).findPersonalizedAssessmentsByCreatorId(requesterId);
        verify(assessmentGateway, never()).findAllPersonalizedAssessments();
    }

    @Test
    void shouldListAllPersonalizedAssessmentsForAdmin() {
        UUID requesterId = UUID.randomUUID();
        UserProfessional admin = admin(requesterId);
        PersonalizedAssessment assessment = assessment(UUID.randomUUID(), recruiter(UUID.randomUUID()));

        when(userGateway.findById(requesterId)).thenReturn(Optional.of(admin));
        when(assessmentGateway.findAllPersonalizedAssessments()).thenReturn(List.of(assessment));

        ListPersonalizedAssessmentsUseCaseImpl useCase =
                new ListPersonalizedAssessmentsUseCaseImpl(assessmentGateway, userGateway);

        List<PersonalizedAssessment> result = useCase.execute(requesterId);

        assertThat(result).containsExactly(assessment);
        verify(assessmentGateway).findAllPersonalizedAssessments();
        verify(assessmentGateway, never()).findPersonalizedAssessmentsByCreatorId(requesterId);
    }

    @Test
    void shouldRejectProfessionalListingPersonalizedAssessments() {
        UUID requesterId = UUID.randomUUID();
        UserProfessional professional = professional(requesterId);

        when(userGateway.findById(requesterId)).thenReturn(Optional.of(professional));

        ListPersonalizedAssessmentsUseCaseImpl useCase =
                new ListPersonalizedAssessmentsUseCaseImpl(assessmentGateway, userGateway);

        assertThatThrownBy(() -> useCase.execute(requesterId))
                .isInstanceOf(AuthorizationException.class);

        verify(assessmentGateway, never()).findAllPersonalizedAssessments();
        verify(assessmentGateway, never()).findPersonalizedAssessmentsByCreatorId(requesterId);
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
                0,
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
                LocalDateTime.now(),
                UserStatus.ACTIVE
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
                LocalDateTime.now(),
                UserStatus.ACTIVE
        );
    }
}
