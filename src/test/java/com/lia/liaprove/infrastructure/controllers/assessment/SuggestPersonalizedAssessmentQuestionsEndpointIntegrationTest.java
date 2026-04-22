package com.lia.liaprove.infrastructure.controllers.assessment;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.CANDIDATE_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.DEV_USER_HEADER;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.RECRUITER_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.deleteAssessmentData;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.getSeededUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = {
        "classpath:db/h2-populate-users.sql",
        "classpath:db/h2-populate-questions.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SuggestPersonalizedAssessmentQuestionsEndpointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
    @DisplayName("Should get suggested questions with filters")
    void shouldGetSuggestedQuestionsWithFilters() throws Exception {
        UserEntity recruiter = getSeededUser(userJpaRepository, RECRUITER_EMAIL);

        mockMvc.perform(get("/api/v1/assessments/personalized/suggestions")
                        .header(DEV_USER_HEADER, recruiter.getEmail())
                        .param("knowledgeAreas", "SOFTWARE_DEVELOPMENT")
                        .param("difficultyLevels", "MEDIUM")
                        .param("questionTypes", "OPEN")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Should return 403 when candidate tries to get suggestions")
    void shouldReturnForbiddenWhenCandidateGetsSuggestions() throws Exception {
        UserEntity candidate = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);

        mockMvc.perform(get("/api/v1/assessments/personalized/suggestions")
                        .header(DEV_USER_HEADER, candidate.getEmail()))
                .andExpect(status().isForbidden());
    }
}
