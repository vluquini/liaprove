package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentGateway;
import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatePersonalizedAssessmentUseCaseImplTest {

    @Mock
    private AssessmentGateway assessmentGateway;

    @Mock
    private QuestionGateway questionGateway;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private CreatePersonalizedAssessmentUseCaseImpl useCase;

    @Test
    void shouldCreatePersonalizedAssessmentWithCriteriaWeights() {
        UUID recruiterId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        UserRecruiter recruiter = recruiter(recruiterId);
        Question question = new MultipleChoiceQuestion();
        question.setId(questionId);

        when(userGateway.findById(recruiterId)).thenReturn(Optional.of(recruiter));
        when(questionGateway.findById(questionId)).thenReturn(Optional.of(question));
        when(assessmentGateway.save(any(PersonalizedAssessment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AssessmentCriteriaWeights weights = new AssessmentCriteriaWeights(50, 30, 20);

        PersonalizedAssessment created = useCase.execute(
                recruiterId,
                "Java assessment",
                "Assessment with explicit weights",
                List.of(questionId),
                LocalDateTime.now().plusDays(5),
                3,
                60,
                weights
        );

        ArgumentCaptor<PersonalizedAssessment> captor = ArgumentCaptor.forClass(PersonalizedAssessment.class);
        verify(assessmentGateway).save(captor.capture());

        PersonalizedAssessment persisted = captor.getValue();
        assertThat(created.getCriteriaWeights()).isEqualTo(weights);
        assertThat(persisted.getCriteriaWeights()).isEqualTo(weights);
    }

    @Test
    void shouldCreatePersonalizedAssessmentWithJobDescriptionAnalysis() {
        JobDescriptionAnalysis analysis = new JobDescriptionAnalysis(
                "Senior Java developer with Spring experience",
                null,
                null,
                null,
                null
        );

        PersonalizedAssessment created = new PersonalizedAssessment(
                UUID.randomUUID(),
                "Java assessment",
                "Assessment with job description analysis",
                LocalDateTime.now(),
                List.of(),
                java.time.Duration.ofMinutes(60),
                null,
                LocalDateTime.now().plusDays(5),
                0,
                3,
                "token",
                null,
                AssessmentCriteriaWeights.defaultWeights(),
                analysis
        );

        assertThat(created.getJobDescriptionAnalysis()).isSameAs(analysis);
        assertThat(created.getJobDescriptionAnalysis().getOriginalJobDescription())
                .isEqualTo("Senior Java developer with Spring experience");
        assertThat(created.getJobDescriptionAnalysis().getSuggestedKnowledgeAreas()).isEmpty();
        assertThat(created.getJobDescriptionAnalysis().getSuggestedHardSkills()).isEmpty();
        assertThat(created.getJobDescriptionAnalysis().getSuggestedSoftSkills()).isEmpty();
        assertThat(created.getJobDescriptionAnalysis().getSuggestedCriteriaWeights())
                .isEqualTo(AssessmentCriteriaWeights.defaultWeights());
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
