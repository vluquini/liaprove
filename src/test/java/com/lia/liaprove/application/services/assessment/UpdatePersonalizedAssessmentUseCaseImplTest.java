package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.domain.user.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatePersonalizedAssessmentUseCaseImplTest {

    @Mock
    private AssessmentGateway assessmentGateway;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private UpdatePersonalizedAssessmentUseCaseImpl useCase;

    @Test
    void shouldUpdateCriteriaWeightsWhenProvided() {
        UUID recruiterId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();

        UserRecruiter recruiter = recruiter(recruiterId);
        PersonalizedAssessment assessment = assessment(assessmentId, recruiter);

        when(userGateway.findById(recruiterId)).thenReturn(Optional.of(recruiter));
        when(assessmentGateway.findById(assessmentId)).thenReturn(Optional.of(assessment));
        when(assessmentGateway.save(any(PersonalizedAssessment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AssessmentCriteriaWeights weights = new AssessmentCriteriaWeights(20, 50, 30);

        PersonalizedAssessment updated = (PersonalizedAssessment) useCase.execute(
                assessmentId,
                recruiterId,
                Optional.empty(),
                Optional.empty(),
                Optional.of(PersonalizedAssessmentStatus.DEACTIVATED),
                Optional.of(weights)
        );

        ArgumentCaptor<PersonalizedAssessment> captor = ArgumentCaptor.forClass(PersonalizedAssessment.class);
        verify(assessmentGateway).save(captor.capture());

        assertThat(updated.getCriteriaWeights()).isEqualTo(weights);
        assertThat(updated.getStatus()).isEqualTo(PersonalizedAssessmentStatus.DEACTIVATED);
        assertThat(captor.getValue().getCriteriaWeights()).isEqualTo(weights);
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
}
