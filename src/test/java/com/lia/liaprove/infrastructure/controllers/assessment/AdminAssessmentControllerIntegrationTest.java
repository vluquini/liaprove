package com.lia.liaprove.infrastructure.controllers.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.assessment.SystemAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.entities.user.UserRecruiterEntity;
import com.lia.liaprove.infrastructure.repositories.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

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
public class AdminAssessmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private AssessmentJpaRepository assessmentJpaRepository;

    @Autowired
    private AssessmentAttemptJpaRepository assessmentAttemptJpaRepository;

    private static final String ADMIN_EMAIL = "admin@liaprove.com";
    private static final String RECRUITER_EMAIL = "ana.p@techrecruit.com";
    private static final String CANDIDATE_EMAIL = "carlos.silva@example.com";

    @BeforeEach
    void setUp() {
        // Criar massa de dados para o Admin listar
        UserEntity recruiter = userJpaRepository.findByEmail(RECRUITER_EMAIL).orElseThrow();
        UserEntity candidate = userJpaRepository.findByEmail(CANDIDATE_EMAIL).orElseThrow();

        // 1. Criar uma Avaliação de Sistema e uma tentativa
        SystemAssessmentEntity systemAssessment = new SystemAssessmentEntity();
        systemAssessment.setTitle("System Java Quiz");
        systemAssessment.setDescription("Auto generated");
        systemAssessment.setCreationDate(LocalDateTime.now().minusDays(1));
        systemAssessment.setEvaluationTimerSeconds(600L);
        assessmentJpaRepository.save(systemAssessment);

        AssessmentAttemptEntity systemAttempt = new AssessmentAttemptEntity();
        systemAttempt.setAssessment(systemAssessment);
        systemAttempt.setUser(candidate);
        systemAttempt.setStartedAt(LocalDateTime.now().minusHours(2));
        systemAttempt.setStatus(AssessmentAttemptStatus.COMPLETED);
        systemAttempt.setAccuracyRate(80);
        assessmentAttemptJpaRepository.save(systemAttempt);

        // 2. Criar uma Avaliação Personalizada e uma tentativa
        PersonalizedAssessmentEntity personalizedAssessment = new PersonalizedAssessmentEntity();
        personalizedAssessment.setTitle("Hiring Challenge");
        personalizedAssessment.setDescription("Custom test");
        personalizedAssessment.setCreatedBy((UserRecruiterEntity) recruiter);
        personalizedAssessment.setCreationDate(LocalDateTime.now().minusDays(2));
        personalizedAssessment.setShareableToken("token-admin-test");
        personalizedAssessment.setStatus(PersonalizedAssessmentStatus.ACTIVE);
        personalizedAssessment.setMaxAttempts(10);
        personalizedAssessment.setEvaluationTimerSeconds(1800L);
        assessmentJpaRepository.save(personalizedAssessment);

        AssessmentAttemptEntity personalizedAttempt = new AssessmentAttemptEntity();
        personalizedAttempt.setAssessment(personalizedAssessment);
        personalizedAttempt.setUser(candidate);
        personalizedAttempt.setStartedAt(LocalDateTime.now().minusMinutes(30));
        personalizedAttempt.setStatus(AssessmentAttemptStatus.IN_PROGRESS);
        assessmentAttemptJpaRepository.save(personalizedAttempt);
    }

    @AfterEach
    void tearDown() {
        assessmentAttemptJpaRepository.deleteAll();
        assessmentJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should list all attempts successfully when user is Admin")
    void listAllAttemptsSuccessAsAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/assessments/attempts")
                        .header("X-Dev-User-Email", ADMIN_EMAIL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].attemptId").exists())
                .andExpect(jsonPath("$[0].assessment.title").exists());
    }

    @Test
    @DisplayName("Should return 403 when user is Recruiter")
    void listAllAttemptsForbiddenAsRecruiter() throws Exception {
        mockMvc.perform(get("/api/v1/admin/assessments/attempts")
                        .header("X-Dev-User-Email", RECRUITER_EMAIL))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 403 when user is Candidate")
    void listAllAttemptsForbiddenAsCandidate() throws Exception {
        mockMvc.perform(get("/api/v1/admin/assessments/attempts")
                        .header("X-Dev-User-Email", CANDIDATE_EMAIL))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should filter attempts by personalized type")
    void listAllAttemptsFilterByPersonalized() throws Exception {
        // isPersonalized = true
        mockMvc.perform(get("/api/v1/admin/assessments/attempts")
                        .header("X-Dev-User-Email", ADMIN_EMAIL)
                        .param("isPersonalized", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].assessment.personalized").value(true));

        // isPersonalized = false
        mockMvc.perform(get("/api/v1/admin/assessments/attempts")
                        .header("X-Dev-User-Email", ADMIN_EMAIL)
                        .param("isPersonalized", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].assessment.personalized").value(false));
    }

    @Test
    @DisplayName("Should filter attempts by status")
    void listAllAttemptsFilterByStatus() throws Exception {
        mockMvc.perform(get("/api/v1/admin/assessments/attempts")
                        .header("X-Dev-User-Email", ADMIN_EMAIL)
                        .param("statuses", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    @Test
    @DisplayName("Should return empty list when filters match nothing")
    void listAllAttemptsEmptyResult() throws Exception {
        mockMvc.perform(get("/api/v1/admin/assessments/attempts")
                        .header("X-Dev-User-Email", ADMIN_EMAIL)
                        .param("statuses", "FAILED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
