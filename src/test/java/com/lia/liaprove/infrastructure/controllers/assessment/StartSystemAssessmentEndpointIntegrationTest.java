package com.lia.liaprove.infrastructure.controllers.assessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.application.services.assessment.dto.SystemAssessmentType;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.infrastructure.dtos.assessment.StartSystemAssessmentRequest;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
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

import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.CANDIDATE_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.DEV_USER_HEADER;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.deleteAssessmentData;
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
class StartSystemAssessmentEndpointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private AssessmentJpaRepository assessmentJpaRepository;

    @Autowired
    private AssessmentAttemptJpaRepository assessmentAttemptJpaRepository;

    @AfterEach
    void tearDown() {
        deleteAssessmentData(assessmentAttemptJpaRepository, assessmentJpaRepository, userJpaRepository);
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
}
