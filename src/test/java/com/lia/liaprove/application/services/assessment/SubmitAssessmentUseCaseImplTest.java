package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.services.assessment.dto.SubmitAssessmentAnswersDto;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.Answer;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.OpenQuestion;
import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.usecases.assessments.IssueCertificateUseCase;
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
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SubmitAssessmentUseCaseImplTest {

    @Mock
    private AssessmentAttemptGateway attemptGateway;

    @Mock
    private IssueCertificateUseCase issueCertificateUseCase;

    @InjectMocks
    private SubmitAssessmentUseCaseImpl useCase;

    @Test
    void shouldPropagateTextResponseForOpenQuestionSubmission() {
        UUID userId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        String textResponse = "Open question answer text.";

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        OpenQuestion question = openQuestion(questionId);
        AssessmentAttempt attempt = personalizedAttempt(attemptId, user, List.of(question));

        when(attemptGateway.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(attemptGateway.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SubmitAssessmentAnswersDto submissionDto = new SubmitAssessmentAnswersDto(
                attemptId,
                List.of(new SubmitAssessmentAnswersDto.QuestionAnswerDto(
                        questionId,
                        null,
                        null,
                        textResponse
                ))
        );

        AssessmentAttempt result = useCase.execute(submissionDto, userId);

        ArgumentCaptor<AssessmentAttempt> attemptCaptor = ArgumentCaptor.forClass(AssessmentAttempt.class);
        verify(attemptGateway).save(attemptCaptor.capture());
        verifyNoInteractions(issueCertificateUseCase);

        assertThat(result.getAnswers()).hasSize(1);
        assertThat(attemptCaptor.getValue().getAnswers()).hasSize(1);
        assertThat(attemptCaptor.getValue().getAnswers().get(0).getSelectedAlternativeId()).isNull();
        assertThat(attemptCaptor.getValue().getAnswers().get(0).getProjectUrl()).isNull();
        assertThat(attemptCaptor.getValue().getAnswers().get(0).getTextResponse()).isEqualTo(textResponse);
    }

    @Test
    void shouldKeepProjectUrlSubmissionWorkingForPersonalizedAssessment() {
        UUID userId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        String projectUrl = "https://github.com/acme/open-question-project";

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        ProjectQuestion question = projectQuestion(questionId);
        AssessmentAttempt attempt = personalizedAttempt(attemptId, user, List.of(question));

        when(attemptGateway.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(attemptGateway.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SubmitAssessmentAnswersDto submissionDto = new SubmitAssessmentAnswersDto(
                attemptId,
                List.of(new SubmitAssessmentAnswersDto.QuestionAnswerDto(
                        questionId,
                        null,
                        projectUrl,
                        null
                ))
        );

        AssessmentAttempt result = useCase.execute(submissionDto, userId);

        ArgumentCaptor<AssessmentAttempt> attemptCaptor = ArgumentCaptor.forClass(AssessmentAttempt.class);
        verify(attemptGateway).save(attemptCaptor.capture());

        assertThat(result.getStatus()).isEqualTo(AssessmentAttemptStatus.COMPLETED);
        assertThat(attemptCaptor.getValue().getAnswers()).hasSize(1);
        assertThat(attemptCaptor.getValue().getAnswers().get(0).getProjectUrl()).isEqualTo(projectUrl);
        assertThat(attemptCaptor.getValue().getAnswers().get(0).getTextResponse()).isNull();
    }

    private AssessmentAttempt personalizedAttempt(UUID attemptId, User user, List<Question> questions) {
        PersonalizedAssessment assessment = new PersonalizedAssessment(
                UUID.randomUUID(),
                "Personalized assessment",
                "desc",
                LocalDateTime.now(),
                questions,
                Duration.ofHours(1),
                null,
                LocalDateTime.now().plusDays(1),
                0,
                3,
                "share-token",
                com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus.ACTIVE
        );

        return new AssessmentAttempt(
                attemptId,
                assessment,
                user,
                questions,
                null,
                null,
                LocalDateTime.now().minusMinutes(10),
                null,
                null,
                null,
                AssessmentAttemptStatus.IN_PROGRESS
        );
    }

    private OpenQuestion openQuestion(UUID questionId) {
        OpenQuestion question = new OpenQuestion("Guideline", OpenQuestionVisibility.SHARED);
        question.setId(questionId);
        question.setAuthorId(UUID.randomUUID());
        question.setTitle("Open question");
        question.setDescription("Open description");
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setSubmissionDate(LocalDateTime.now());
        question.setVotingEndDate(LocalDateTime.now().plusDays(2));
        question.setStatus(QuestionStatus.APPROVED);
        question.setRelevanceByLLM(RelevanceLevel.FOUR);
        question.setRecruiterUsageCount(0);
        return question;
    }

    private ProjectQuestion projectQuestion(UUID questionId) {
        ProjectQuestion question = new ProjectQuestion();
        question.setId(questionId);
        question.setAuthorId(UUID.randomUUID());
        question.setTitle("Project question");
        question.setDescription("Project description");
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setSubmissionDate(LocalDateTime.now());
        question.setVotingEndDate(LocalDateTime.now().plusDays(2));
        question.setStatus(QuestionStatus.APPROVED);
        question.setRelevanceByLLM(RelevanceLevel.FOUR);
        question.setRecruiterUsageCount(0);
        return question;
    }
}
