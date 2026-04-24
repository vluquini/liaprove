package com.lia.liaprove.infrastructure.controllers.assessment;

import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.entities.user.UserRecruiterEntity;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentJpaRepository;
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

import java.util.List;

import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.CANDIDATE_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.DEV_USER_HEADER;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.RECRUITER_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.createPersonalizedAssessment;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.deleteAssessmentData;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.getSeededMultipleChoiceQuestion;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.getSeededRecruiter;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.getSeededUser;
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
class StartPersonalizedAssessmentEndpointIntegrationTest {

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

    @AfterEach
    void tearDown() {
        deleteAssessmentData(assessmentAttemptJpaRepository, assessmentJpaRepository, userJpaRepository);
    }

    @Test
    @DisplayName("Should start personalized assessment successfully")
    void shouldStartPersonalizedAssessmentSuccessfully() throws Exception {
        UserEntity candidate = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);
        UserRecruiterEntity recruiter = getSeededRecruiter(userJpaRepository, RECRUITER_EMAIL);
        QuestionEntity question = getSeededMultipleChoiceQuestion(questionJpaRepository);

        PersonalizedAssessmentEntity assessment = createPersonalizedAssessment(assessmentJpaRepository, recruiter);
        assessment.setTitle("Tech Test 2026");
        assessment.setDescription("Test for Java Developers");
        assessment.setQuestions(List.of(question));
        assessment = assessmentJpaRepository.save(assessment);

        mockMvc.perform(post("/api/v1/assessments/start-personalized/" + assessment.getShareableToken())
                        .header(DEV_USER_HEADER, candidate.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.attemptId").exists());
    }

    @Test
    @DisplayName("Should return 404 when starting personalized assessment with invalid token")
    void shouldReturnNotFoundWhenStartingWithInvalidToken() throws Exception {
        UserEntity user = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);

        mockMvc.perform(post("/api/v1/assessments/start-personalized/invalid-token-123")
                        .header(DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 401 when starting personalized assessment without authentication")
    void shouldReturnUnauthorizedWhenStartingPersonalizedAssessmentWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/v1/assessments/start-personalized/some-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
