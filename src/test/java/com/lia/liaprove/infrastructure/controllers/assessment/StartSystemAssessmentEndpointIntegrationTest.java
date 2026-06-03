package com.lia.liaprove.infrastructure.controllers.assessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.lia.liaprove.application.services.assessment.dto.SystemAssessmentType;
import com.lia.liaprove.core.domain.metrics.VoteType;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.infrastructure.dtos.assessment.StartSystemAssessmentRequest;
import com.lia.liaprove.infrastructure.entities.metrics.VoteEntity;
import com.lia.liaprove.infrastructure.entities.question.AlternativeEntity;
import com.lia.liaprove.infrastructure.entities.question.MultipleChoiceQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.metrics.VoteJpaRepository;
import com.lia.liaprove.infrastructure.repositories.question.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
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
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.CANDIDATE_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.DEV_USER_HEADER;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.RECRUITER_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.getSeededUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = {
        "classpath:db/h2-populate-users.sql",
        "classpath:db/h2-populate-questions.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class StartSystemAssessmentEndpointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @Autowired
    private VoteJpaRepository voteJpaRepository;

    @Autowired
    private AssessmentJpaRepository assessmentJpaRepository;

    @Autowired
    private AssessmentAttemptJpaRepository assessmentAttemptJpaRepository;

    @AfterEach
    void tearDown() {
        assessmentAttemptJpaRepository.deleteAll();
        assessmentJpaRepository.deleteAll();
        voteJpaRepository.deleteAll();
        questionJpaRepository.findAll().stream()
                .filter(question -> question.getTitle().startsWith("Eligibility Integration"))
                .forEach(questionJpaRepository::delete);
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should start system assessment successfully")
    void shouldStartSystemAssessmentSuccessfully() throws Exception {
        UserEntity user = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);
        StartSystemAssessmentRequest request = new StartSystemAssessmentRequest(
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                DifficultyLevel.MEDIUM,
                SystemAssessmentType.MULTIPLE_CHOICE
        );

        mockMvc.perform(post("/api/v1/assessments/start-system")
                        .header(DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.attemptId").exists());
    }

    @Test
    @DisplayName("Should not include questions authored or voted by the user in system assessment")
    void shouldExcludeAuthoredAndVotedQuestionsFromSystemAssessment() throws Exception {
        UserEntity user = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);
        UserEntity otherUser = getSeededUser(userJpaRepository, RECRUITER_EMAIL);
        hideSeededQuestionsFromSystemAssessmentPool();

        MultipleChoiceQuestionEntity authoredQuestion = createFinishedMultipleChoiceQuestion(
                user,
                "Authored question"
        );
        MultipleChoiceQuestionEntity votedQuestion = createFinishedMultipleChoiceQuestion(
                otherUser,
                "Voted question"
        );
        MultipleChoiceQuestionEntity eligibleQuestion = createFinishedMultipleChoiceQuestion(
                otherUser,
                "Eligible question"
        );
        createVote(user, votedQuestion);

        StartSystemAssessmentRequest request = new StartSystemAssessmentRequest(
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                DifficultyLevel.MEDIUM,
                SystemAssessmentType.MULTIPLE_CHOICE
        );

        MvcResult result = mockMvc.perform(post("/api/v1/assessments/start-system")
                        .header(DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        List<String> questionIds = JsonPath.read(result.getResponse().getContentAsString(), "$.questions[*].id");

        assertThat(questionIds)
                .containsExactly(eligibleQuestion.getId().toString())
                .doesNotContain(authoredQuestion.getId().toString(), votedQuestion.getId().toString());
    }

    @Test
    @DisplayName("Should return 400 when starting system assessment with null knowledge area")
    void shouldReturnBadRequestWhenStartingWithNullKnowledgeArea() throws Exception {
        UserEntity user = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);
        StartSystemAssessmentRequest request = new StartSystemAssessmentRequest(
                null,
                DifficultyLevel.MEDIUM,
                SystemAssessmentType.MULTIPLE_CHOICE
        );

        mockMvc.perform(post("/api/v1/assessments/start-system")
                        .header(DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 401 when starting system assessment without authentication")
    void shouldReturnUnauthorizedWhenStartingSystemAssessmentWithoutAuth() throws Exception {
        StartSystemAssessmentRequest request = new StartSystemAssessmentRequest(
                KnowledgeArea.SOFTWARE_DEVELOPMENT,
                DifficultyLevel.MEDIUM,
                SystemAssessmentType.MULTIPLE_CHOICE
        );

        mockMvc.perform(post("/api/v1/assessments/start-system")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    private void hideSeededQuestionsFromSystemAssessmentPool() {
        List<QuestionEntity> questions = questionJpaRepository.findAll();
        questions.forEach(question -> question.setStatus(QuestionStatus.VOTING));
        questionJpaRepository.saveAll(questions);
    }

    private MultipleChoiceQuestionEntity createFinishedMultipleChoiceQuestion(UserEntity author, String title) {
        MultipleChoiceQuestionEntity question = new MultipleChoiceQuestionEntity();
        question.setAuthorId(author.getId());
        question.setTitle("Eligibility Integration " + title);
        question.setDescription("Eligibility Integration " + title + " " + UUID.randomUUID());
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setRelevanceByLLM(RelevanceLevel.THREE);
        question.setSubmissionDate(LocalDateTime.now());
        question.setVotingEndDate(LocalDateTime.now().minusDays(1));
        question.setStatus(QuestionStatus.FINISHED);
        question.setRecruiterUsageCount(0);
        question.addAlternative(createAlternative("Correct answer", true));
        question.addAlternative(createAlternative("Wrong answer", false));
        return questionJpaRepository.save(question);
    }

    private AlternativeEntity createAlternative(String text, boolean correct) {
        AlternativeEntity alternative = new AlternativeEntity();
        alternative.setText(text);
        alternative.setCorrect(correct);
        return alternative;
    }

    private void createVote(UserEntity user, QuestionEntity question) {
        VoteEntity vote = new VoteEntity();
        vote.setUser(user);
        vote.setQuestion(question);
        vote.setVoteType(VoteType.APPROVE);
        vote.setCreatedAt(LocalDateTime.now());
        voteJpaRepository.save(vote);
    }
}
