package com.lia.liaprove.infrastructure.controllers.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.infrastructure.entities.assessment.AnswerEntity;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.assessment.SystemAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.metrics.AssessmentAttemptVoteEntity;
import com.lia.liaprove.infrastructure.entities.question.ProjectQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.entities.user.UserRecruiterEntity;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.repositories.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.AssessmentAttemptVoteJpaRepository;
import com.lia.liaprove.infrastructure.repositories.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.FeedbackAssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.FeedbackQuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import com.lia.liaprove.infrastructure.repositories.VoteJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev") // Ensure dev profile is active for H2 config and DevAuthenticationFilter
@Sql(scripts = {"classpath:db/h2-populate-users.sql", "classpath:db/h2-populate-questions.sql"},
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MetricsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @Autowired
    private FeedbackQuestionJpaRepository feedbackQuestionJpaRepository;

    @Autowired
    private VoteJpaRepository voteJpaRepository;

    @Autowired
    private AssessmentJpaRepository assessmentJpaRepository;

    @Autowired
    private AssessmentAttemptJpaRepository assessmentAttemptJpaRepository;

    @Autowired
    private AssessmentAttemptVoteJpaRepository assessmentAttemptVoteJpaRepository;

    @Autowired
    private FeedbackAssessmentJpaRepository feedbackAssessmentJpaRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @AfterEach
    void tearDown() {
        assessmentAttemptVoteJpaRepository.deleteAll();
        assessmentAttemptJpaRepository.deleteAll();
        voteJpaRepository.deleteAll();
        feedbackAssessmentJpaRepository.deleteAll();
        feedbackQuestionJpaRepository.deleteAll();
        assessmentJpaRepository.deleteAll();
        questionJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    // New utility method to get a seeded user entity
    private UserEntity getSeededUserEntity(String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Seeded user not found: " + email));
    }

    private QuestionEntity createTestQuestion() {
        MultipleChoiceQuestion questionDomain = new MultipleChoiceQuestion(
                List.of(
                        new Alternative(null, "Alternative A", false),
                        new Alternative(null, "Alternative B", true)
                )
        );
        questionDomain.setAuthorId(UUID.randomUUID());
        questionDomain.setTitle("Test Question Title - " + UUID.randomUUID()); // Ensure unique title
        questionDomain.setDescription("Test question description. - " + UUID.randomUUID()); // Ensure unique description
        questionDomain.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        questionDomain.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        questionDomain.setStatus(QuestionStatus.VOTING);
        questionDomain.setSubmissionDate(LocalDateTime.now());
        questionDomain.setVotingEndDate(LocalDateTime.now().plusDays(7));
        questionDomain.setRelevanceByCommunity(RelevanceLevel.THREE);
        questionDomain.setRelevanceByLLM(RelevanceLevel.FOUR);
        questionDomain.setRecruiterUsageCount(0);


        QuestionEntity entity = questionMapper.toEntity(questionDomain);
        return questionJpaRepository.save(entity);
    }

    private PersonalizedAssessmentEntity createTestAssessment(UserEntity recruiter) {
        PersonalizedAssessmentEntity assessment = new PersonalizedAssessmentEntity();
        assessment.setTitle("Assessment " + UUID.randomUUID());
        assessment.setDescription("Assessment description");
        assessment.setCreationDate(LocalDateTime.now());
        assessment.setCreatedBy((UserRecruiterEntity) recruiter);
        assessment.setShareableToken("token-" + UUID.randomUUID());
        assessment.setStatus(PersonalizedAssessmentStatus.ACTIVE);
        assessment.setMaxAttempts(3);
        assessment.setTotalAttempts(0);
        assessment.setEvaluationTimerSeconds(1800L);
        return assessmentJpaRepository.save(assessment);
    }

    private ProjectQuestionEntity createProjectQuestionEntity() {
        ProjectQuestionEntity question = new ProjectQuestionEntity();
        question.setAuthorId(UUID.randomUUID());
        question.setTitle("Project Question " + UUID.randomUUID());
        question.setDescription("Project question description " + UUID.randomUUID());
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setSubmissionDate(LocalDateTime.now().minusDays(2));
        question.setVotingEndDate(LocalDateTime.now().plusDays(2));
        question.setStatus(QuestionStatus.APPROVED);
        question.setRelevanceByLLM(RelevanceLevel.FOUR);
        question.setRecruiterUsageCount(0);
        return questionJpaRepository.save(question);
    }

    private AssessmentAttemptEntity createFinishedSystemProjectAttempt(UserEntity owner, String repositoryLink) {
        ProjectQuestionEntity projectQuestion = createProjectQuestionEntity();

        SystemAssessmentEntity assessment = new SystemAssessmentEntity();
        assessment.setTitle("System Project Assessment " + UUID.randomUUID());
        assessment.setDescription("System project assessment description");
        assessment.setCreationDate(LocalDateTime.now().minusDays(1));
        assessment.setEvaluationTimerSeconds(3600L);
        assessment.setQuestions(List.of(projectQuestion));
        SystemAssessmentEntity savedAssessment = assessmentJpaRepository.save(assessment);

        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity();
        attempt.setAssessment(savedAssessment);
        attempt.setUser(owner);
        attempt.setQuestions(List.of(projectQuestion));
        attempt.setStartedAt(LocalDateTime.now().minusHours(2));
        attempt.setFinishedAt(LocalDateTime.now().minusHours(1));
        attempt.setStatus(AssessmentAttemptStatus.COMPLETED);
        attempt.setAccuracyRate(0);

        AnswerEntity answer = new AnswerEntity();
        answer.setQuestionId(projectQuestion.getId());
        answer.setProjectUrl(repositoryLink);
        attempt.addAnswer(answer);

        return assessmentAttemptJpaRepository.save(attempt);
    }

    private AssessmentAttemptEntity createUnfinishedSystemProjectAttempt(UserEntity owner, String repositoryLink) {
        AssessmentAttemptEntity attempt = createFinishedSystemProjectAttempt(owner, repositoryLink);
        attempt.setFinishedAt(null);
        attempt.setStatus(AssessmentAttemptStatus.IN_PROGRESS);
        return assessmentAttemptJpaRepository.save(attempt);
    }

    private AssessmentAttemptEntity createSystemMultipleChoiceAttempt(UserEntity owner) {
        QuestionEntity multipleChoiceQuestion = createTestQuestion();

        SystemAssessmentEntity assessment = new SystemAssessmentEntity();
        assessment.setTitle("System Multiple Choice " + UUID.randomUUID());
        assessment.setDescription("System multiple choice description");
        assessment.setCreationDate(LocalDateTime.now().minusDays(1));
        assessment.setEvaluationTimerSeconds(1800L);
        assessment.setQuestions(List.of(multipleChoiceQuestion));
        SystemAssessmentEntity savedAssessment = assessmentJpaRepository.save(assessment);

        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity();
        attempt.setAssessment(savedAssessment);
        attempt.setUser(owner);
        attempt.setQuestions(List.of(multipleChoiceQuestion));
        attempt.setStartedAt(LocalDateTime.now().minusHours(2));
        attempt.setFinishedAt(LocalDateTime.now().minusHours(1));
        attempt.setStatus(AssessmentAttemptStatus.APPROVED);
        attempt.setAccuracyRate(100);

        AnswerEntity answer = new AnswerEntity();
        answer.setQuestionId(multipleChoiceQuestion.getId());
        answer.setSelectedAlternativeId(UUID.randomUUID());
        attempt.addAnswer(answer);

        return assessmentAttemptJpaRepository.save(attempt);
    }

    @Test
    @DisplayName("Should cast a vote on a question successfully")
    void shouldCastVoteSuccessfully() throws Exception {
                // Setup
                UserEntity user = getSeededUserEntity("mariana.costa@example.com");
                QuestionEntity question = questionJpaRepository.findAll().get(0); // Get a seeded question
        
                com.lia.liaprove.infrastructure.dtos.metrics.CastVoteRequest voteRequest = new com.lia.liaprove.infrastructure.dtos.metrics.CastVoteRequest();
                voteRequest.setVoteType(com.lia.liaprove.core.domain.metrics.VoteType.APPROVE);
        
                // Act
                mockMvc.perform(post("/api/v1/questions/{questionId}/vote", question.getId())
                                .header("X-Dev-User-Email", user.getEmail())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(voteRequest)))
                        .andExpect(status().isOk());
        
                // Assert: A deeper assertion would require a VoteJpaRepository and a method to find votes.
                // For now, we trust the 200 OK status indicates success as the use case is called.
                // A more complete test would be:
                // Vote vote = voteRepository.findByUserIdAndQuestionId(...).orElseThrow();
                // assertThat(vote.getVoteType()).isEqualTo(VoteType.APPROVE);
            }

    @Test
    @DisplayName("Should list only finished system mini-project attempts from other users")
    void shouldListOnlyFinishedSystemMiniProjectAttemptsFromOtherUsers() throws Exception {
        UserEntity reviewer = getSeededUserEntity("mariana.costa@example.com");
        UserEntity owner = getSeededUserEntity("carlos.silva@example.com");

        AssessmentAttemptEntity visibleAttempt = createFinishedSystemProjectAttempt(owner, "https://github.com/acme/project-reviewable");
        createUnfinishedSystemProjectAttempt(owner, "https://github.com/acme/project-unfinished");
        createFinishedSystemProjectAttempt(reviewer, "https://github.com/acme/project-own");
        createSystemMultipleChoiceAttempt(owner);

        mockMvc.perform(get("/api/v1/assessment-attempts/mini-project/public")
                        .header("X-Dev-User-Email", reviewer.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].attemptId").value(visibleAttempt.getId().toString()))
                .andExpect(jsonPath("$[0].repositoryLink").value("https://github.com/acme/project-reviewable"));
    }

    @Test
    @DisplayName("Should cast vote on assessment attempt successfully")
    void shouldCastVoteOnAssessmentAttemptSuccessfully() throws Exception {
        UserEntity reviewer = getSeededUserEntity("mariana.costa@example.com");
        UserEntity owner = getSeededUserEntity("carlos.silva@example.com");
        AssessmentAttemptEntity attempt = createFinishedSystemProjectAttempt(owner, "https://github.com/acme/project-vote");

        com.lia.liaprove.infrastructure.dtos.metrics.CastVoteRequest voteRequest = new com.lia.liaprove.infrastructure.dtos.metrics.CastVoteRequest();
        voteRequest.setVoteType(com.lia.liaprove.core.domain.metrics.VoteType.APPROVE);

        mockMvc.perform(post("/api/v1/assessment-attempts/{attemptId}/vote", attempt.getId())
                        .header("X-Dev-User-Email", reviewer.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isOk());

        List<AssessmentAttemptVoteEntity> votes = assessmentAttemptVoteJpaRepository.findAll();
        assertThat(votes).hasSize(1);
        assertThat(votes.get(0).getAssessmentAttempt().getId()).isEqualTo(attempt.getId());
        assertThat(votes.get(0).getUser().getId()).isEqualTo(reviewer.getId());
        assertThat(votes.get(0).getVoteType()).isEqualTo(com.lia.liaprove.core.domain.metrics.VoteType.APPROVE);
    }

    @Test
    @DisplayName("Should reject duplicate vote on same assessment attempt")
    void shouldRejectDuplicateVoteOnSameAssessmentAttempt() throws Exception {
        UserEntity reviewer = getSeededUserEntity("mariana.costa@example.com");
        UserEntity owner = getSeededUserEntity("carlos.silva@example.com");
        AssessmentAttemptEntity attempt = createFinishedSystemProjectAttempt(owner, "https://github.com/acme/project-duplicate-vote");

        com.lia.liaprove.infrastructure.dtos.metrics.CastVoteRequest voteRequest = new com.lia.liaprove.infrastructure.dtos.metrics.CastVoteRequest();
        voteRequest.setVoteType(com.lia.liaprove.core.domain.metrics.VoteType.APPROVE);

        mockMvc.perform(post("/api/v1/assessment-attempts/{attemptId}/vote", attempt.getId())
                        .header("X-Dev-User-Email", reviewer.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/assessment-attempts/{attemptId}/vote", attempt.getId())
                        .header("X-Dev-User-Email", reviewer.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reject vote on own assessment attempt")
    void shouldRejectVoteOnOwnAssessmentAttempt() throws Exception {
        UserEntity owner = getSeededUserEntity("carlos.silva@example.com");
        AssessmentAttemptEntity attempt = createFinishedSystemProjectAttempt(owner, "https://github.com/acme/project-own-vote");

        com.lia.liaprove.infrastructure.dtos.metrics.CastVoteRequest voteRequest = new com.lia.liaprove.infrastructure.dtos.metrics.CastVoteRequest();
        voteRequest.setVoteType(com.lia.liaprove.core.domain.metrics.VoteType.REJECT);

        mockMvc.perform(post("/api/v1/assessment-attempts/{attemptId}/vote", attempt.getId())
                        .header("X-Dev-User-Email", owner.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isForbidden());
    }

}
