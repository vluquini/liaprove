package com.lia.liaprove.infrastructure.controllers.assessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.infrastructure.dtos.assessment.EvaluateAssessmentAttemptRequest;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
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
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.RECRUITER_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.createAttempt;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.createPersonalizedAssessment;
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
class EvaluateAssessmentAttemptEndpointIntegrationTest {

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
    @DisplayName("Should evaluate attempt successfully")
    void shouldEvaluateAttemptSuccessfully() throws Exception {
        UserEntity recruiter = getSeededUser(userJpaRepository, RECRUITER_EMAIL);
        UserEntity candidate = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);
        PersonalizedAssessmentEntity assessment = createPersonalizedAssessment(assessmentJpaRepository, recruiter);
        AssessmentAttemptEntity attempt = createAttempt(
                assessmentAttemptJpaRepository,
                assessment,
                candidate,
                AssessmentAttemptStatus.COMPLETED
        );
        EvaluateAssessmentAttemptRequest request = new EvaluateAssessmentAttemptRequest(AssessmentAttemptStatus.APPROVED);

        mockMvc.perform(post("/api/v1/assessments/" + attempt.getId() + "/evaluate")
                        .header(DEV_USER_HEADER, recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attemptId").value(attempt.getId().toString()))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.assessment.id").value(assessment.getId().toString()))
                .andExpect(jsonPath("$.assessment.personalized").value(true))
                .andExpect(jsonPath("$.candidate.id").value(candidate.getId().toString()))
                .andExpect(jsonPath("$.explainability.totalQuestions").value(0))
                .andExpect(jsonPath("$.explainability.answeredQuestions").value(0))
                .andExpect(jsonPath("$.explainability.candidateExperienceLevel").value("SENIOR"));
    }
}
