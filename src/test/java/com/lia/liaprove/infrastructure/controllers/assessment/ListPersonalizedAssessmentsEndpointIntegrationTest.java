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

import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.ADMIN_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.CANDIDATE_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.DEV_USER_HEADER;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.OTHER_RECRUITER_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.RECRUITER_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.createPersonalizedAssessment;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.deleteAssessmentData;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.getSeededUser;
import static org.hamcrest.Matchers.containsInAnyOrder;
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
class ListPersonalizedAssessmentsEndpointIntegrationTest {

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
    @DisplayName("Should list only recruiter's own personalized assessments")
    void shouldListOnlyRecruiterOwnAssessments() throws Exception {
        UserEntity recruiter = getSeededUser(userJpaRepository, RECRUITER_EMAIL);
        UserEntity otherRecruiter = getSeededUser(userJpaRepository, OTHER_RECRUITER_EMAIL);
        PersonalizedAssessmentEntity ownAssessment = createPersonalizedAssessment(assessmentJpaRepository, recruiter);
        ownAssessment.setTitle("Ana Backend Challenge");
        assessmentJpaRepository.save(ownAssessment);

        PersonalizedAssessmentEntity otherAssessment = createPersonalizedAssessment(assessmentJpaRepository, otherRecruiter);
        otherAssessment.setTitle("Roberto Data Challenge");
        assessmentJpaRepository.save(otherAssessment);

        mockMvc.perform(get("/api/v1/assessments/personalized")
                        .header(DEV_USER_HEADER, recruiter.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(ownAssessment.getId().toString()))
                .andExpect(jsonPath("$[0].title").value("Ana Backend Challenge"))
                .andExpect(jsonPath("$[0].createdBy.email").value(RECRUITER_EMAIL));
    }

    @Test
    @DisplayName("Should list all personalized assessments for admin")
    void shouldListAllAssessmentsForAdmin() throws Exception {
        UserEntity recruiter = getSeededUser(userJpaRepository, RECRUITER_EMAIL);
        UserEntity otherRecruiter = getSeededUser(userJpaRepository, OTHER_RECRUITER_EMAIL);
        UserEntity admin = getSeededUser(userJpaRepository, ADMIN_EMAIL);
        PersonalizedAssessmentEntity ownAssessment = createPersonalizedAssessment(assessmentJpaRepository, recruiter);
        ownAssessment.setTitle("Ana Backend Challenge");
        assessmentJpaRepository.save(ownAssessment);

        PersonalizedAssessmentEntity otherAssessment = createPersonalizedAssessment(assessmentJpaRepository, otherRecruiter);
        otherAssessment.setTitle("Roberto Data Challenge");
        assessmentJpaRepository.save(otherAssessment);

        mockMvc.perform(get("/api/v1/assessments/personalized")
                        .header(DEV_USER_HEADER, admin.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder(
                        "Ana Backend Challenge",
                        "Roberto Data Challenge"
                )));
    }

    @Test
    @DisplayName("Should return 403 when professional lists personalized assessments")
    void shouldReturnForbiddenWhenProfessionalListsAssessments() throws Exception {
        UserEntity candidate = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);

        mockMvc.perform(get("/api/v1/assessments/personalized")
                        .header(DEV_USER_HEADER, candidate.getEmail()))
                .andExpect(status().isForbidden());
    }
}
