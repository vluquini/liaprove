package com.lia.liaprove.infrastructure.controllers.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.CANDIDATE_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.DEV_USER_HEADER;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.RECRUITER_EMAIL;
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
class ListPersonalizedAssessmentAttemptsEndpointIntegrationTest {

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
    @DisplayName("Should list attempts for assessment")
    void shouldListAttemptsSuccessfully() throws Exception {
        UserEntity recruiter = getSeededUser(userJpaRepository, RECRUITER_EMAIL);
        UserEntity candidate = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);

        PersonalizedAssessmentEntity assessment = createPersonalizedAssessment(assessmentJpaRepository, recruiter);
        assessment.setHardSkillsWeight(55);
        assessment.setSoftSkillsWeight(25);
        assessment.setExperienceWeight(20);
        assessment.setOriginalJobDescription("Backend engineer focused on Java, architecture and communication.");
        assessment.setSuggestedKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        assessment.setSuggestedHardSkills(List.of("Java", "Spring Boot"));
        assessment.setSuggestedSoftSkills(List.of("Communication"));
        assessment.setSuggestedHardSkillsWeight(50);
        assessment.setSuggestedSoftSkillsWeight(30);
        assessment.setSuggestedExperienceWeight(20);
        assessment = assessmentJpaRepository.save(assessment);

        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity();
        attempt.setAssessment(assessment);
        attempt.setUser(candidate);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setStatus(AssessmentAttemptStatus.COMPLETED);
        assessmentAttemptJpaRepository.save(attempt);

        mockMvc.perform(get("/api/v1/assessments/personalized/" + assessment.getId() + "/attempts")
                        .header(DEV_USER_HEADER, recruiter.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].assessment.criteriaWeights.hardSkillsWeight").value(55))
                .andExpect(jsonPath("$[0].assessment.criteriaWeights.softSkillsWeight").value(25))
                .andExpect(jsonPath("$[0].assessment.criteriaWeights.experienceWeight").value(20))
                .andExpect(jsonPath("$[0].assessment.jobDescriptionAnalysis.originalJobDescription")
                        .value("Backend engineer focused on Java, architecture and communication."))
                .andExpect(jsonPath("$[0].assessment.jobDescriptionAnalysis.suggestedHardSkills[0]").value("Java"))
                .andExpect(jsonPath("$[0].assessment.jobDescriptionAnalysis.suggestedCriteriaWeights.hardSkillsWeight")
                        .value(50));
    }
}
