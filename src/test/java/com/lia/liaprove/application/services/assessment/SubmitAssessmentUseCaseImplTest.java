package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.application.services.assessment.dto.SubmitAssessmentAnswersDto;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.Certificate;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.assessment.SystemAssessment;
import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

    @Mock
    private UserGateway userGateway;

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
        verifyNoInteractions(userGateway);

        assertThat(result.getAnswers()).hasSize(1);
        assertThat(attemptCaptor.getValue().getAnswers()).hasSize(1);
        assertThat(attemptCaptor.getValue().getAnswers().getFirst().getSelectedAlternativeId()).isNull();
        assertThat(attemptCaptor.getValue().getAnswers().getFirst().getProjectUrl()).isNull();
        assertThat(attemptCaptor.getValue().getAnswers().getFirst().getTextResponse()).isEqualTo(textResponse);
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
        verifyNoInteractions(userGateway);

        assertThat(result.getStatus()).isEqualTo(AssessmentAttemptStatus.COMPLETED);
        assertThat(attemptCaptor.getValue().getAnswers()).hasSize(1);
        assertThat(attemptCaptor.getValue().getAnswers().getFirst().getProjectUrl()).isEqualTo(projectUrl);
        assertThat(attemptCaptor.getValue().getAnswers().getFirst().getTextResponse()).isNull();
    }

    @Test
    void shouldNotIssueCertificateWhenSystemAssessmentAlreadyHasSameOrHigherScoreForCriteria() {
        UUID userId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        QuestionAnswerPair question = questionAnswerPair();
        AssessmentAttempt attempt = systemAttempt(
                attemptId,
                user,
                List.of(question.question())
        );
        AssessmentAttempt existingCertifiedAttempt = certifiedAttempt(
                user,
                100
        );

        when(attemptGateway.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(attemptGateway.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(attemptGateway.findBestCertifiedSystemAttemptByUserAndCriteria(
                userId,
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                DifficultyLevel.MEDIUM
        )).thenReturn(Optional.of(existingCertifiedAttempt));

        SubmitAssessmentAnswersDto submissionDto = new SubmitAssessmentAnswersDto(
                attemptId,
                List.of(new SubmitAssessmentAnswersDto.QuestionAnswerDto(
                        question.question().getId(),
                        question.correctAlternativeId(),
                        null,
                        null
                ))
        );

        AssessmentAttempt result = useCase.execute(submissionDto, userId);

        assertThat(result.getStatus()).isEqualTo(AssessmentAttemptStatus.APPROVED);
        assertThat(result.getAccuracyRate()).isEqualTo(100);
        assertThat(result.getCertificate()).isNull();
        verify(user).recordAssessmentResult(100f);
        verify(userGateway).save(user);
        verify(issueCertificateUseCase, never()).execute(any());
        verify(attemptGateway, times(1)).save(any());
    }

    @Test
    void shouldIssueCertificateWhenSystemAssessmentImprovesExistingScoreForCriteria() {
        UUID userId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        QuestionAnswerPair question = questionAnswerPair();
        AssessmentAttempt attempt = systemAttempt(
                attemptId,
                user,
                List.of(question.question())
        );
        AssessmentAttempt existingCertifiedAttempt = certifiedAttempt(
                user,
                70
        );
        Certificate newCertificate = new Certificate(
                UUID.randomUUID(),
                "CERT-NEW",
                "Certificado de Conclusão",
                "Certificado emitido pelo LIA Prove.",
                "https://liaprove.com/certificates/CERT-NEW",
                java.time.LocalDate.now(),
                100F
        );

        when(attemptGateway.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(attemptGateway.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(attemptGateway.findBestCertifiedSystemAttemptByUserAndCriteria(
                userId,
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                DifficultyLevel.MEDIUM
        )).thenReturn(Optional.of(existingCertifiedAttempt));
        when(issueCertificateUseCase.execute(any())).thenReturn(newCertificate);

        SubmitAssessmentAnswersDto submissionDto = new SubmitAssessmentAnswersDto(
                attemptId,
                List.of(new SubmitAssessmentAnswersDto.QuestionAnswerDto(
                        question.question().getId(),
                        question.correctAlternativeId(),
                        null,
                        null
                ))
        );

        AssessmentAttempt result = useCase.execute(submissionDto, userId);

        assertThat(result.getStatus()).isEqualTo(AssessmentAttemptStatus.APPROVED);
        assertThat(result.getCertificate()).isEqualTo(newCertificate);
        verify(user).recordAssessmentResult(100f);
        verify(userGateway).save(user);
        verify(issueCertificateUseCase).execute(any());
        verify(attemptGateway, times(2)).save(any());
    }

    @Test
    void shouldRecordUserAssessmentMetricsWhenSystemAssessmentFails() {
        UUID userId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        QuestionAnswerPair question = questionAnswerPair();
        AssessmentAttempt attempt = systemAttempt(
                attemptId,
                user,
                List.of(question.question())
        );

        when(attemptGateway.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(attemptGateway.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SubmitAssessmentAnswersDto submissionDto = new SubmitAssessmentAnswersDto(
                attemptId,
                List.of(new SubmitAssessmentAnswersDto.QuestionAnswerDto(
                        question.question().getId(),
                        UUID.randomUUID(),
                        null,
                        null
                ))
        );

        AssessmentAttempt result = useCase.execute(submissionDto, userId);

        assertThat(result.getStatus()).isEqualTo(AssessmentAttemptStatus.FAILED);
        assertThat(result.getAccuracyRate()).isZero();
        verify(user).recordAssessmentResult(0f);
        verify(userGateway).save(user);
        verifyNoInteractions(issueCertificateUseCase);
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

    private AssessmentAttempt systemAttempt(
            UUID attemptId,
            User user,
            List<Question> questions
    ) {
        SystemAssessment assessment = new SystemAssessment(
                UUID.randomUUID(),
                "System assessment",
                "desc",
                LocalDateTime.now(),
                questions,
                Duration.ofMinutes(10),
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                DifficultyLevel.MEDIUM
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

    private AssessmentAttempt certifiedAttempt(
            User user,
            int score
    ) {
        AssessmentAttempt attempt = systemAttempt(
                UUID.randomUUID(),
                user,
                List.of()
        );
        attempt.setStatus(AssessmentAttemptStatus.APPROVED);
        attempt.setAccuracyRate(score);
        attempt.setCertificate(new Certificate(
                UUID.randomUUID(),
                "CERT-" + score,
                "Certificado de Conclusão",
                "Certificado emitido pelo LIA Prove.",
                "https://liaprove.com/certificates/CERT-" + score,
                java.time.LocalDate.now(),
                (float) score
        ));
        return attempt;
    }

    private QuestionAnswerPair questionAnswerPair() {
        UUID questionId = UUID.randomUUID();
        UUID correctAlternativeId = UUID.randomUUID();
        UUID wrongAlternativeId = UUID.randomUUID();
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(List.of(
                new Alternative(correctAlternativeId, "Correct", true),
                new Alternative(wrongAlternativeId, "Wrong", false)
        ));
        question.setId(questionId);
        question.setTitle("Multiple choice question");
        question.setDescription("Multiple choice description");
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setSubmissionDate(LocalDateTime.now());
        question.setVotingEndDate(LocalDateTime.now().plusDays(2));
        question.setStatus(QuestionStatus.APPROVED);
        question.setRelevanceByLLM(RelevanceLevel.FOUR);
        question.setRecruiterUsageCount(0);
        return new QuestionAnswerPair(question, correctAlternativeId);
    }

    private record QuestionAnswerPair(MultipleChoiceQuestion question, UUID correctAlternativeId) {
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
