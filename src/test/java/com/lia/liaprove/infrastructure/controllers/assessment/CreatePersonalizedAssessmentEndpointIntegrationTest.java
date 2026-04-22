package com.lia.liaprove.infrastructure.controllers.assessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.infrastructure.dtos.assessment.CreatePersonalizedAssessmentRequest;
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

import java.time.LocalDateTime;
import java.util.List;

import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.CANDIDATE_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.DEV_USER_HEADER;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.RECRUITER_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.SEEDED_MULTIPLE_CHOICE_QUESTION_ID;
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
class CreatePersonalizedAssessmentEndpointIntegrationTest {

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
    @DisplayName("Should create personalized assessment successfully")
    void shouldCreatePersonalizedAssessmentSuccessfully() throws Exception {
        UserEntity recruiter = getSeededUser(userJpaRepository, RECRUITER_EMAIL);
        CreatePersonalizedAssessmentRequest request = new CreatePersonalizedAssessmentRequest(
                "Java Developer Test",
                "Assessment for Java positions",
                List.of(SEEDED_MULTIPLE_CHOICE_QUESTION_ID),
                LocalDateTime.now().plusDays(7),
                3,
                60
        );

        mockMvc.perform(post("/api/v1/assessments/personalized")
                        .header(DEV_USER_HEADER, recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Should return 403 when candidate tries to create personalized assessment")
    void shouldReturnForbiddenWhenCandidateCreatesAssessment() throws Exception {
        UserEntity candidate = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);
        CreatePersonalizedAssessmentRequest request = new CreatePersonalizedAssessmentRequest(
                "Invalid",
                "Should not work",
                List.of(SEEDED_MULTIPLE_CHOICE_QUESTION_ID),
                LocalDateTime.now().plusDays(5),
                1,
                30
        );

        mockMvc.perform(post("/api/v1/assessments/personalized")
                        .header(DEV_USER_HEADER, candidate.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 400 when creating assessment without title")
    void shouldReturnBadRequestWhenCreatingAssessmentWithoutTitle() throws Exception {
        UserEntity recruiter = getSeededUser(userJpaRepository, RECRUITER_EMAIL);
        CreatePersonalizedAssessmentRequest request = new CreatePersonalizedAssessmentRequest(
                null,
                "desc",
                List.of(),
                LocalDateTime.now().plusDays(5),
                1,
                30
        );

        mockMvc.perform(post("/api/v1/assessments/personalized")
                        .header(DEV_USER_HEADER, recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
