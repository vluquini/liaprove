package com.lia.liaprove.infrastructure.controllers.assessment;

import com.lia.liaprove.application.gateways.ai.AttemptPreAnalysisContext;
import com.lia.liaprove.application.gateways.ai.AttemptPreAnalysisGateway;
import com.lia.liaprove.application.gateways.ai.JobDescriptionAnalysisGateway;
import com.lia.liaprove.application.gateways.ai.QuestionPreAnalysisGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.AttemptPreAnalysis;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.question.OpenQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.ProjectQuestionEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.entities.user.UserRecruiterEntity;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.question.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.sql.init.mode=never")
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = "classpath:db/h2-populate-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AssessmentControllerAttemptPreAnalysisIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @Autowired
    private AssessmentJpaRepository assessmentJpaRepository;

    @Autowired
    private AssessmentAttemptJpaRepository assessmentAttemptJpaRepository;

    @MockitoBean(
            extraInterfaces = {QuestionPreAnalysisGateway.class, JobDescriptionAnalysisGateway.class}
    )
    private AttemptPreAnalysisGateway attemptPreAnalysisGateway;

    @Test
    void shouldReturnForbiddenWhenAnotherRecruiterRequestsAttemptPreAnalysis() throws Exception {
        UserRecruiterEntity recruiterOwner = recruiter("ana.p@techrecruit.com");
        UserEntity recruiterRequester = user("roberto.l@hiredev.com");
        UserEntity candidate = user("carlos.silva@example.com");

        AssessmentAttemptEntity attempt = createAttempt(
                recruiterOwner,
                candidate,
                List.of(savedProjectQuestion(recruiterOwner, "Project question for forbidden access"))
        );

        mockMvc.perform(post("/api/v1/assessments/attempts/" + attempt.getId() + "/pre-analysis")
                        .header("X-Dev-User-Email", recruiterRequester.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(attemptPreAnalysisGateway);
    }

    @Test
    void shouldReturnBadRequestWhenAttemptHasOnlyProjectQuestions() throws Exception {
        UserRecruiterEntity recruiter = recruiter("ana.p@techrecruit.com");
        UserEntity candidate = user("carlos.silva@example.com");

        AssessmentAttemptEntity attempt = createAttempt(
                recruiter,
                candidate,
                List.of(savedProjectQuestion(recruiter, "Project question only"))
        );

        mockMvc.perform(post("/api/v1/assessments/attempts/" + attempt.getId() + "/pre-analysis")
                        .header("X-Dev-User-Email", recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value("/api/v1/assessments/attempts/" + attempt.getId() + "/pre-analysis"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.error")
                        .value("Attempt " + attempt.getId() + " does not contain supported questions for pre-analysis."));

        verifyNoInteractions(attemptPreAnalysisGateway);
    }

    @Test
    void shouldExposeIgnoredProjectQuestionTypeWhenAttemptIsMixed() throws Exception {
        UserRecruiterEntity recruiter = recruiter("ana.p@techrecruit.com");
        UserEntity candidate = user("carlos.silva@example.com");

        OpenQuestionEntity openQuestion = savedOpenQuestion(recruiter, "Open question for mixed attempt");
        ProjectQuestionEntity projectQuestion = savedProjectQuestion(recruiter, "Project question for mixed attempt");

        AssessmentAttemptEntity attempt = createAttempt(
                recruiter,
                candidate,
                List.of(openQuestion, projectQuestion)
        );

        when(attemptPreAnalysisGateway.generate(any())).thenAnswer(invocation -> {
            AttemptPreAnalysisContext context = invocation.getArgument(0);

            assertThat(context.attemptId()).isEqualTo(attempt.getId());
            assertThat(context.supportedQuestions()).hasSize(1);
            assertThat(context.supportedQuestions().getFirst().questionType()).isEqualTo(QuestionType.OPEN);
            assertThat(context.ignoredQuestionTypes()).containsExactly(QuestionType.PROJECT);

            return new AttemptPreAnalysis.Analysis(
                    "Candidate shows solid technical reasoning.",
                    List.of("Clear problem decomposition", "Good trade-off awareness"),
                    List.of("Should improve API validation"),
                    "Overall, the attempt is above average and ready for recruiter review."
            );
        });

        mockMvc.perform(post("/api/v1/assessments/attempts/" + attempt.getId() + "/pre-analysis")
                        .header("X-Dev-User-Email", recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata.attemptId").value(attempt.getId().toString()))
                .andExpect(jsonPath("$.metadata.generatedAt").exists())
                .andExpect(jsonPath("$.metadata.ignoredQuestionTypes[0]").value("PROJECT"))
                .andExpect(jsonPath("$.analysis.summary").value("Candidate shows solid technical reasoning."))
                .andExpect(jsonPath("$.analysis.strengths[0]").value("Clear problem decomposition"))
                .andExpect(jsonPath("$.analysis.attentionPoints[0]").value("Should improve API validation"))
                .andExpect(jsonPath("$.analysis.finalExplanation")
                        .value("Overall, the attempt is above average and ready for recruiter review."));

        verify(attemptPreAnalysisGateway, times(1)).generate(any());
        verifyNoMoreInteractions(attemptPreAnalysisGateway);
    }

    @Test
    void shouldReturnForbiddenWhenCandidateRequestsPreAnalysis() throws Exception {
        UserRecruiterEntity recruiter = recruiter("ana.p@techrecruit.com");
        UserEntity candidate = user("carlos.silva@example.com");

        AssessmentAttemptEntity attempt = createAttempt(
                recruiter,
                candidate,
                List.of(savedOpenQuestion(recruiter, "Open question for candidate forbidden access"))
        );

        mockMvc.perform(post("/api/v1/assessments/attempts/" + attempt.getId() + "/pre-analysis")
                        .header("X-Dev-User-Email", candidate.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(attemptPreAnalysisGateway);
    }

    private UserRecruiterEntity recruiter(String email) {
        return (UserRecruiterEntity) userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Seeded recruiter not found: " + email));
    }

    private UserEntity user(String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Seeded user not found: " + email));
    }

    private OpenQuestionEntity savedOpenQuestion(UserRecruiterEntity recruiter, String title) {
        OpenQuestionEntity question = new OpenQuestionEntity();
        question.setAuthorId(recruiter.getId());
        question.setTitle(title);
        question.setDescription(title + " description");
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setSubmissionDate(LocalDateTime.now());
        question.setVotingEndDate(LocalDateTime.now().plusDays(7));
        question.setStatus(QuestionStatus.FINISHED);
        question.setRelevanceByLLM(RelevanceLevel.THREE);
        question.setRecruiterUsageCount(0);
        question.setGuideline("Focus on technical reasoning.");
        question.setVisibility(OpenQuestionVisibility.SHARED);
        return (OpenQuestionEntity) questionJpaRepository.save(question);
    }

    private ProjectQuestionEntity savedProjectQuestion(UserRecruiterEntity recruiter, String title) {
        ProjectQuestionEntity question = new ProjectQuestionEntity();
        question.setAuthorId(recruiter.getId());
        question.setTitle(title);
        question.setDescription(title + " description");
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.FOUR);
        question.setSubmissionDate(LocalDateTime.now());
        question.setVotingEndDate(LocalDateTime.now().plusDays(7));
        question.setStatus(QuestionStatus.FINISHED);
        question.setRelevanceByLLM(RelevanceLevel.FOUR);
        question.setRecruiterUsageCount(0);
        question.setProjectUrl("https://example.com/" + UUID.randomUUID());
        return (ProjectQuestionEntity) questionJpaRepository.save(question);
    }

    private AssessmentAttemptEntity createAttempt(
            UserRecruiterEntity recruiter,
            UserEntity candidate,
            List<? extends com.lia.liaprove.infrastructure.entities.question.QuestionEntity> questions) {
        PersonalizedAssessmentEntity assessment = new PersonalizedAssessmentEntity();
        UUID randomSuffix = UUID.randomUUID();
        assessment.setTitle("Assessment " + randomSuffix);
        assessment.setDescription("Pre-analysis integration test assessment");
        assessment.setCreationDate(LocalDateTime.now());
        assessment.setCreatedBy(recruiter);
        assessment.setShareableToken("token-" + randomSuffix);
        assessment.setStatus(PersonalizedAssessmentStatus.ACTIVE);
        assessment.setMaxAttempts(3);
        assessment.setEvaluationTimerSeconds(1800L);
        assessment.setQuestions(questions.stream()
                .map(q -> (com.lia.liaprove.infrastructure.entities.question.QuestionEntity) q)
                .toList());
        assessment = assessmentJpaRepository.save(assessment);

        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity();
        attempt.setAssessment(assessment);
        attempt.setUser(candidate);
        attempt.setQuestions(questions.stream()
                .map(q -> (com.lia.liaprove.infrastructure.entities.question.QuestionEntity) q)
                .toList());
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setStatus(AssessmentAttemptStatus.COMPLETED);
        attempt.setFinishedAt(LocalDateTime.now());
        return assessmentAttemptJpaRepository.save(attempt);
    }
}
