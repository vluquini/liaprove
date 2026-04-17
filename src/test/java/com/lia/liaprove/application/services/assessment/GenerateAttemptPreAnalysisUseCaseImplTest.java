package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.ai.AttemptPreAnalysisGateway;
import com.lia.liaprove.application.gateways.ai.AttemptPreAnalysisContext;
import com.lia.liaprove.application.gateways.assessment.AssessmentAttemptGateway;
import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.core.domain.assessment.Answer;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.AttemptPreAnalysis;
import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.domain.assessment.SystemAssessment;
import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.OpenQuestion;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserStatus;
import com.lia.liaprove.core.exceptions.assessment.AttemptPreAnalysisInProgressException;
import com.lia.liaprove.core.exceptions.assessment.AttemptPreAnalysisNotAvailableException;
import com.lia.liaprove.core.exceptions.user.AuthorizationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateAttemptPreAnalysisUseCaseImplTest {

    @Mock
    private AssessmentAttemptGateway attemptGateway;

    @Mock
    private UserGateway userGateway;

    @Mock
    private AttemptPreAnalysisGateway attemptPreAnalysisGateway;

    private GenerateAttemptPreAnalysisUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GenerateAttemptPreAnalysisUseCaseImpl(attemptGateway, userGateway, attemptPreAnalysisGateway);
    }

    @Test
    void shouldGeneratePreAnalysisForOwnedPersonalizedAssessmentAttempt() {
        UUID attemptId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UUID candidateId = UUID.randomUUID();
        Alternative correctAlternative = new Alternative(UUID.fromString("22222222-2222-2222-2222-222222222222"), "Clear root cause analysis", true);
        Alternative incorrectAlternative = new Alternative(UUID.fromString("33333333-3333-3333-3333-333333333333"), "Generic answer", false);
        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion(List.of(correctAlternative, incorrectAlternative));
        multipleChoiceQuestion.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        multipleChoiceQuestion.setTitle("Debugging workflow");
        multipleChoiceQuestion.setDescription("Choose the best explanation for the failure.");
        OpenQuestion openQuestion = new OpenQuestion();
        openQuestion.setId(UUID.fromString("44444444-4444-4444-4444-444444444444"));
        openQuestion.setTitle("Architecture rationale");
        openQuestion.setDescription("Explain the trade-offs in the current design.");
        openQuestion.setGuideline("Discuss coupling, testability, and boundaries.");
        ProjectQuestion projectQuestion = new ProjectQuestion();
        Answer mcqAnswer = new Answer(multipleChoiceQuestion.getId());
        mcqAnswer.setSelectedAlternativeId(correctAlternative.id());
        Answer openAnswer = new Answer(openQuestion.getId());
        openAnswer.setTextResponse("The current design favors testability over reuse.");
        UserRecruiter requester = recruiter(requesterId);
        UserProfessional candidate = professional(candidateId);
        AssessmentCriteriaWeights criteriaWeights = new AssessmentCriteriaWeights(50, 30, 20);
        JobDescriptionAnalysis jobDescriptionAnalysis = new JobDescriptionAnalysis(
                "Senior backend role focused on Java and APIs",
                java.util.Set.of(),
                List.of("Java", "Spring Boot"),
                List.of("Communication", "Ownership"),
                criteriaWeights
        );
        AssessmentAttempt attempt = buildRichAttempt(
                attemptId,
                requester,
                candidate,
                List.of(multipleChoiceQuestion, openQuestion, projectQuestion),
                List.of(mcqAnswer, openAnswer),
                AssessmentAttemptStatus.COMPLETED,
                83,
                criteriaWeights,
                jobDescriptionAnalysis
        );
        AttemptPreAnalysis.Analysis analysis = analysis();

        when(attemptGateway.findByIdWithCreator(attemptId)).thenReturn(java.util.Optional.of(attempt));
        when(userGateway.findById(requesterId)).thenReturn(java.util.Optional.of(requester));
        when(attemptPreAnalysisGateway.generate(any(AttemptPreAnalysisContext.class))).thenReturn(analysis);

        LocalDateTime before = LocalDateTime.now();
        AttemptPreAnalysis result = useCase.execute(attemptId, requesterId);
        LocalDateTime after = LocalDateTime.now();

        ArgumentCaptor<AttemptPreAnalysisContext> contextCaptor = contextCaptor();
        verify(attemptPreAnalysisGateway).generate(contextCaptor.capture());

        assertThat(contextCaptor.getValue().attemptId()).isEqualTo(attemptId);
        assertThat(contextCaptor.getValue().attemptStatus()).isEqualTo(AssessmentAttemptStatus.COMPLETED);
        assertThat(contextCaptor.getValue().accuracyRate()).isEqualTo(83);
        assertThat(contextCaptor.getValue().assessment().title()).isEqualTo("Personalized");
        assertThat(contextCaptor.getValue().assessment().criteriaWeights()).isEqualTo(criteriaWeights);
        assertThat(contextCaptor.getValue().assessment().jobDescriptionAnalysis()).isEqualTo(jobDescriptionAnalysis);
        assertThat(contextCaptor.getValue().candidate().experienceLevel()).isEqualTo(ExperienceLevel.SENIOR);
        assertThat(contextCaptor.getValue().candidate().hardSkills()).containsExactly("Java", "Spring Boot");
        assertThat(contextCaptor.getValue().candidate().softSkills()).containsExactly("Communication", "Ownership");
        assertThat(contextCaptor.getValue().supportedQuestions()).hasSize(2);
        assertThat(contextCaptor.getValue().supportedQuestions().get(0).questionType()).isEqualTo(QuestionType.MULTIPLE_CHOICE);
        assertThat(contextCaptor.getValue().supportedQuestions().get(0).selectedAlternativeId()).isEqualTo(correctAlternative.id());
        assertThat(contextCaptor.getValue().supportedQuestions().get(0).selectedAlternativeText()).isEqualTo("Clear root cause analysis");
        assertThat(contextCaptor.getValue().supportedQuestions().get(0).textResponse()).isNull();
        assertThat(contextCaptor.getValue().supportedQuestions().get(0).alternatives()).hasSize(2);
        assertThat(contextCaptor.getValue().supportedQuestions().get(1).questionType()).isEqualTo(QuestionType.OPEN);
        assertThat(contextCaptor.getValue().supportedQuestions().get(1).guideline()).isEqualTo("Discuss coupling, testability, and boundaries.");
        assertThat(contextCaptor.getValue().supportedQuestions().get(1).textResponse()).isEqualTo("The current design favors testability over reuse.");
        assertThat(contextCaptor.getValue().supportedQuestions().get(1).alternatives()).isEmpty();
        assertThat(contextCaptor.getValue().ignoredQuestionTypes()).containsExactly(QuestionType.PROJECT);
        assertThat(result.getMetadata().getAttemptId()).isEqualTo(attemptId);
        assertThat(result.getMetadata().getGeneratedAt()).isBetween(before, after);
        assertThat(result.getMetadata().getIgnoredQuestionTypes()).containsExactly(QuestionType.PROJECT);
        assertThat(result.getAnalysis()).isSameAs(analysis);
    }

    @Test
    void shouldRejectAttemptWithoutSupportedQuestions() {
        UUID requesterId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        AssessmentAttempt attempt = buildAttempt(attemptId, requesterId, List.of(new ProjectQuestion()));
        UserRecruiter requester = recruiter(requesterId);

        when(attemptGateway.findByIdWithCreator(attemptId)).thenReturn(java.util.Optional.of(attempt));
        when(userGateway.findById(requesterId)).thenReturn(java.util.Optional.of(requester));

        assertThatThrownBy(() -> useCase.execute(attemptId, requesterId))
                .isInstanceOf(AttemptPreAnalysisNotAvailableException.class);

        verify(attemptPreAnalysisGateway, never()).generate(any(AttemptPreAnalysisContext.class));
    }

    @Test
    void shouldRejectAttemptFromSystemAssessment() {
        UUID requesterId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        AssessmentAttempt attempt = new AssessmentAttempt(
                attemptId,
                new SystemAssessment(UUID.randomUUID(), "System", "System assessment", LocalDateTime.now(), List.of(new MultipleChoiceQuestion()), Duration.ofMinutes(20)),
                recruiter(requesterId),
                List.of(new MultipleChoiceQuestion()),
                List.of(),
                List.of(),
                LocalDateTime.now(),
                null,
                null,
                null,
                AssessmentAttemptStatus.IN_PROGRESS
        );
        UserRecruiter requester = recruiter(requesterId);

        when(attemptGateway.findByIdWithCreator(attemptId)).thenReturn(java.util.Optional.of(attempt));
        when(userGateway.findById(requesterId)).thenReturn(java.util.Optional.of(requester));

        assertThatThrownBy(() -> useCase.execute(attemptId, requesterId))
                .isInstanceOf(AuthorizationException.class);

        verify(attemptPreAnalysisGateway, never()).generate(any(AttemptPreAnalysisContext.class));
    }

    @Test
    void shouldRejectRecruiterWhoDoesNotOwnAssessment() {
        UUID ownerId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        AssessmentAttempt attempt = buildAttempt(attemptId, ownerId, List.of(new MultipleChoiceQuestion()));
        UserRecruiter requester = recruiter(requesterId);

        when(attemptGateway.findByIdWithCreator(attemptId)).thenReturn(java.util.Optional.of(attempt));
        when(userGateway.findById(requesterId)).thenReturn(java.util.Optional.of(requester));

        assertThatThrownBy(() -> useCase.execute(attemptId, requesterId))
                .isInstanceOf(AuthorizationException.class);

        verify(attemptPreAnalysisGateway, never()).generate(any(AttemptPreAnalysisContext.class));
    }

    @Test
    void shouldIgnoreProjectQuestionsAndSendOnlySupportedQuestions() {
        UUID requesterId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion();
        ProjectQuestion projectQuestion = new ProjectQuestion();
        AssessmentAttempt attempt = buildAttempt(attemptId, requesterId, List.of(multipleChoiceQuestion, projectQuestion));
        UserRecruiter requester = recruiter(requesterId);
        AttemptPreAnalysis.Analysis analysis = analysis();

        when(attemptGateway.findByIdWithCreator(attemptId)).thenReturn(java.util.Optional.of(attempt));
        when(userGateway.findById(requesterId)).thenReturn(java.util.Optional.of(requester));
        when(attemptPreAnalysisGateway.generate(any(AttemptPreAnalysisContext.class))).thenReturn(analysis);

        useCase.execute(attemptId, requesterId);

        ArgumentCaptor<AttemptPreAnalysisContext> contextCaptor = contextCaptor();
        verify(attemptPreAnalysisGateway).generate(contextCaptor.capture());
        assertThat(contextCaptor.getValue().supportedQuestions()).extracting(AttemptPreAnalysisContext.QuestionContext::questionType)
                .containsExactly(QuestionType.MULTIPLE_CHOICE);
    }

    @Test
    void shouldRejectWhenGenerationIsAlreadyInProgressForSameAttempt() throws Exception {
        UUID requesterId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        AssessmentAttempt attempt = buildAttempt(attemptId, requesterId, List.of(new MultipleChoiceQuestion()));
        UserRecruiter requester = recruiter(requesterId);
        AttemptPreAnalysis.Analysis analysis = analysis();
        CountDownLatch gatewayEntered = new CountDownLatch(1);
        CountDownLatch releaseGateway = new CountDownLatch(1);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            when(attemptGateway.findByIdWithCreator(attemptId)).thenReturn(java.util.Optional.of(attempt));
            when(userGateway.findById(requesterId)).thenReturn(java.util.Optional.of(requester));
            when(attemptPreAnalysisGateway.generate(any(AttemptPreAnalysisContext.class))).thenAnswer(invocation -> {
                gatewayEntered.countDown();
                if (!releaseGateway.await(5, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Timed out waiting for the first generation to be released.");
                }
                return analysis;
            });

            java.util.concurrent.Future<AttemptPreAnalysis> firstCall = executor.submit(() -> useCase.execute(attemptId, requesterId));
            assertThat(gatewayEntered.await(5, TimeUnit.SECONDS)).isTrue();

            assertThatThrownBy(() -> useCase.execute(attemptId, requesterId))
                    .isInstanceOf(AttemptPreAnalysisInProgressException.class);

            releaseGateway.countDown();
            assertThat(firstCall.get(5, TimeUnit.SECONDS)).isNotNull();
        } finally {
            releaseGateway.countDown();
            executor.shutdownNow();
        }
    }

    @Test
    void shouldReleaseLockWhenGatewayFails() throws Exception {
        UUID requesterId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        AssessmentAttempt attempt = buildAttempt(attemptId, requesterId, List.of(new MultipleChoiceQuestion()));
        UserRecruiter requester = recruiter(requesterId);
        CountDownLatch firstInvocationStarted = new CountDownLatch(1);
        CountDownLatch allowFailure = new CountDownLatch(1);
        AtomicInteger gatewayCalls = new AtomicInteger();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            when(attemptGateway.findByIdWithCreator(attemptId)).thenReturn(java.util.Optional.of(attempt));
            when(userGateway.findById(requesterId)).thenReturn(java.util.Optional.of(requester));
            when(attemptPreAnalysisGateway.generate(any(AttemptPreAnalysisContext.class))).thenAnswer(invocation -> {
                gatewayCalls.incrementAndGet();
                firstInvocationStarted.countDown();
                if (!allowFailure.await(5, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Timed out waiting to release gateway failure.");
                }
                throw new IllegalStateException("gateway failed");
            });

            java.util.concurrent.Future<?> firstCall = executor.submit(() -> useCase.execute(attemptId, requesterId));
            assertThat(firstInvocationStarted.await(5, TimeUnit.SECONDS)).isTrue();

            allowFailure.countDown();
            assertThatThrownBy(firstCall::get)
                    .hasCauseInstanceOf(IllegalStateException.class)
                    .hasRootCauseMessage("gateway failed");

            assertThatThrownBy(() -> useCase.execute(attemptId, requesterId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("gateway failed");
        } finally {
            allowFailure.countDown();
            executor.shutdownNow();
        }

        assertThat(gatewayCalls.get()).isEqualTo(2);
    }

    private AttemptPreAnalysis.Analysis analysis() {
        return new AttemptPreAnalysis.Analysis(
                "summary",
                List.of("strength"),
                List.of("attention"),
                "final explanation"
        );
    }

    private ArgumentCaptor<AttemptPreAnalysisContext> contextCaptor() {
        return ArgumentCaptor.forClass(AttemptPreAnalysisContext.class);
    }

    private UserProfessional professional(UUID professionalId) {
        UserProfessional professional = new UserProfessional(
                professionalId,
                "Candidate",
                "candidate@example.com",
                "hashed-password",
                "Developer",
                "Bio",
                ExperienceLevel.SENIOR,
                UserRole.PROFESSIONAL,
                1,
                3,
                List.of(),
                82.0f,
                LocalDateTime.now(),
                LocalDateTime.now(),
                UserStatus.ACTIVE
        );
        professional.setHardSkills(List.of("Java", "Spring Boot"));
        professional.setSoftSkills(List.of("Communication", "Ownership"));
        return professional;
    }

    private AssessmentAttempt buildRichAttempt(
            UUID attemptId,
            UserRecruiter requester,
            UserProfessional candidate,
            List<Question> questions,
            List<Answer> answers,
            AssessmentAttemptStatus status,
            Integer accuracyRate,
            AssessmentCriteriaWeights criteriaWeights,
            JobDescriptionAnalysis jobDescriptionAnalysis) {
        PersonalizedAssessment assessment = new PersonalizedAssessment(
                UUID.randomUUID(),
                "Personalized",
                "Personalized assessment",
                LocalDateTime.now(),
                questions,
                Duration.ofMinutes(30),
                requester,
                LocalDateTime.now().plusDays(1),
                0,
                1,
                "token",
                PersonalizedAssessmentStatus.ACTIVE,
                criteriaWeights,
                jobDescriptionAnalysis
        );

        return new AssessmentAttempt(
                attemptId,
                assessment,
                candidate,
                questions,
                answers,
                List.of(),
                LocalDateTime.now(),
                null,
                accuracyRate,
                null,
                status
        );
    }

    private AssessmentAttempt buildAttempt(UUID attemptId, UUID requesterId, List<Question> questions) {
        UserRecruiter recruiter = recruiter(requesterId);
        PersonalizedAssessment assessment = new PersonalizedAssessment(
                UUID.randomUUID(),
                "Personalized",
                "Personalized assessment",
                LocalDateTime.now(),
                questions,
                Duration.ofMinutes(30),
                recruiter,
                LocalDateTime.now().plusDays(1),
                0,
                1,
                "token",
                PersonalizedAssessmentStatus.ACTIVE,
                null
        );

        return new AssessmentAttempt(
                attemptId,
                assessment,
                recruiter,
                questions,
                List.of(),
                List.of(),
                LocalDateTime.now(),
                null,
                null,
                null,
                AssessmentAttemptStatus.IN_PROGRESS
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
