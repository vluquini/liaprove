package com.lia.liaprove.infrastructure.mappers.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.infrastructure.dtos.assessment.PersonalizedAssessmentDetailsResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AssessmentDtoMapperTest {

    private final AssessmentDtoMapper mapper = Mappers.getMapper(AssessmentDtoMapper.class);

    @Test
    void shouldMapPersonalizedAssessmentDetails() {
        UUID assessmentId = UUID.randomUUID();
        UUID recruiterId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        LocalDateTime creationDate = LocalDateTime.now().minusDays(1);
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(7);
        LocalDateTime submissionDate = LocalDateTime.now().minusDays(2);

        UserRecruiter recruiter = recruiter(recruiterId);
        Question question = question(questionId, submissionDate);
        AssessmentCriteriaWeights weights = new AssessmentCriteriaWeights(60, 25, 15);
        JobDescriptionAnalysis analysis = new JobDescriptionAnalysis(
                "Senior Java backend developer",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT, KnowledgeArea.DATABASE),
                List.of("Java", "Spring"),
                List.of("Communication"),
                weights
        );
        PersonalizedAssessment assessment = new PersonalizedAssessment(
                assessmentId,
                "Backend assessment",
                "Technical assessment",
                creationDate,
                List.of(question),
                Duration.ofMinutes(45),
                recruiter,
                expirationDate,
                5,
                "token-123",
                PersonalizedAssessmentStatus.ACTIVE,
                weights,
                analysis
        );
        assessment.setTotalAttempts(2);

        PersonalizedAssessmentDetailsResponse response = mapper.toPersonalizedDetailsResponse(assessment);

        assertThat(response.id()).isEqualTo(assessmentId);
        assertThat(response.title()).isEqualTo("Backend assessment");
        assertThat(response.evaluationTimerMinutes()).isEqualTo(45);
        assertThat(response.expirationDate()).isEqualTo(expirationDate);
        assertThat(response.totalAttempts()).isEqualTo(2);
        assertThat(response.maxAttempts()).isEqualTo(5);
        assertThat(response.shareableToken()).isEqualTo("token-123");
        assertThat(response.status()).isEqualTo(PersonalizedAssessmentStatus.ACTIVE);
        assertThat(response.createdBy().id()).isEqualTo(recruiterId);
        assertThat(response.criteriaWeights().hardSkillsWeight()).isEqualTo(60);
        assertThat(response.jobDescriptionAnalysis().suggestedHardSkills()).containsExactly("Java", "Spring");
        assertThat(response.questions()).hasSize(1);
        assertThat(response.questions().getFirst().getId()).isEqualTo(questionId);
        assertThat(response.questions().getFirst().getKnowledgeAreas()).containsExactly(KnowledgeArea.SOFTWARE_DEVELOPMENT);
        assertThat(response.questions().getFirst().getSubmissionDate()).isEqualTo(submissionDate);
    }

    @Test
    void shouldReturnEmptyQuestionListWhenAssessmentQuestionsAreEmpty() {
        PersonalizedAssessment assessment = new PersonalizedAssessment(
                UUID.randomUUID(),
                "Assessment",
                "Description",
                LocalDateTime.now(),
                List.of(),
                Duration.ofMinutes(30),
                recruiter(UUID.randomUUID()),
                LocalDateTime.now().plusDays(7),
                1,
                "token",
                PersonalizedAssessmentStatus.ACTIVE,
                AssessmentCriteriaWeights.defaultWeights()
        );

        PersonalizedAssessmentDetailsResponse response = mapper.toPersonalizedDetailsResponse(assessment);

        assertThat(response.questions()).isEmpty();
        assertThat(response.evaluationTimerMinutes()).isEqualTo(30);
    }

    private Question question(UUID id, LocalDateTime submissionDate) {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(List.of());
        question.setId(id);
        question.setAuthorId(UUID.randomUUID());
        question.setTitle("Question");
        question.setDescription("Description");
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setSubmissionDate(submissionDate);
        return question;
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
}
