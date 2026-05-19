package com.lia.liaprove.infrastructure.controllers.assessment;

import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.ADMIN_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.DEV_USER_HEADER;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.OTHER_RECRUITER_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.RECRUITER_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.applyJobDescriptionAnalysis;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.createPersonalizedAssessment;
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
class GetPersonalizedAssessmentEndpointIntegrationTest {

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
    @DisplayName("Should get own personalized assessment as recruiter")
    void shouldGetOwnAssessmentAsRecruiter() throws Exception {
        UserEntity recruiter = getSeededUser(userJpaRepository, RECRUITER_EMAIL);
        PersonalizedAssessmentEntity assessment = createPersonalizedAssessment(assessmentJpaRepository, recruiter);
        assessment.setTitle("Ana Backend Challenge");
        applyJobDescriptionAnalysis(assessment);
        assessment = assessmentJpaRepository.save(assessment);

        mockMvc.perform(get("/api/v1/assessments/personalized/" + assessment.getId())
                        .header(DEV_USER_HEADER, recruiter.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(assessment.getId().toString()))
                .andExpect(jsonPath("$.title").value("Ana Backend Challenge"))
                .andExpect(jsonPath("$.createdBy.email").value(RECRUITER_EMAIL))
                .andExpect(jsonPath("$.criteriaWeights.hardSkillsWeight").value(50))
                .andExpect(jsonPath("$.jobDescriptionAnalysis.suggestedHardSkills[0]").value("Java"));
    }

    @Test
    @DisplayName("Should return 403 when recruiter gets another recruiter's assessment")
    void shouldReturnForbiddenWhenRecruiterGetsAnotherRecruiterAssessment() throws Exception {
        UserEntity recruiter = getSeededUser(userJpaRepository, RECRUITER_EMAIL);
        UserEntity otherRecruiter = getSeededUser(userJpaRepository, OTHER_RECRUITER_EMAIL);
        PersonalizedAssessmentEntity assessment = createPersonalizedAssessment(assessmentJpaRepository, recruiter);

        mockMvc.perform(get("/api/v1/assessments/personalized/" + assessment.getId())
                        .header(DEV_USER_HEADER, otherRecruiter.getEmail()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should get any personalized assessment as admin")
    void shouldGetAnyAssessmentAsAdmin() throws Exception {
        UserEntity recruiter = getSeededUser(userJpaRepository, RECRUITER_EMAIL);
        UserEntity admin = getSeededUser(userJpaRepository, ADMIN_EMAIL);
        PersonalizedAssessmentEntity assessment = createPersonalizedAssessment(assessmentJpaRepository, recruiter);
        assessment.setTitle("Admin Visible Challenge");
        assessment = assessmentJpaRepository.save(assessment);

        mockMvc.perform(get("/api/v1/assessments/personalized/" + assessment.getId())
                        .header(DEV_USER_HEADER, admin.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(assessment.getId().toString()))
                .andExpect(jsonPath("$.title").value("Admin Visible Challenge"));
    }

    @Test
    @DisplayName("Should return 404 when personalized assessment does not exist")
    void shouldReturnNotFoundWhenAssessmentDoesNotExist() throws Exception {
        UserEntity recruiter = getSeededUser(userJpaRepository, RECRUITER_EMAIL);

        mockMvc.perform(get("/api/v1/assessments/personalized/" + UUID.randomUUID())
                        .header(DEV_USER_HEADER, recruiter.getEmail()))
                .andExpect(status().isNotFound());
    }
}
