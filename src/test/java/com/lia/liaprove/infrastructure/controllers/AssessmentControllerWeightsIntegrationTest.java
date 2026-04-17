package com.lia.liaprove.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.infrastructure.dtos.assessment.CreatePersonalizedAssessmentRequest;
import com.lia.liaprove.infrastructure.dtos.assessment.UpdatePersonalizedAssessmentRequest;
import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.entities.user.UserRecruiterEntity;
import com.lia.liaprove.infrastructure.repositories.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
class AssessmentControllerWeightsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private AssessmentJpaRepository assessmentJpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        assessmentJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create personalized assessment with criteria weights")
    void shouldCreatePersonalizedAssessmentWithCriteriaWeights() throws Exception {
        UserEntity recruiter = getSeededUserEntity("ana.p@techrecruit.com");
        UUID questionId = UUID.fromString("00000001-0000-0000-0000-000000000001");

        CreatePersonalizedAssessmentRequest request = new CreatePersonalizedAssessmentRequest(
                "Weighted Java Developer Test",
                "Assessment for weighted evaluation",
                List.of(questionId),
                LocalDateTime.now().plusDays(7),
                3,
                60,
                45,
                35,
                20
        );

        mockMvc.perform(post("/api/v1/assessments/personalized")
                        .header("X-Dev-User-Email", recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.criteriaWeights.hardSkillsWeight").value(45))
                .andExpect(jsonPath("$.criteriaWeights.softSkillsWeight").value(35))
                .andExpect(jsonPath("$.criteriaWeights.experienceWeight").value(20));
    }

    @Test
    @DisplayName("Should create personalized assessment with persisted job description analysis snapshot")
    void shouldCreatePersonalizedAssessmentWithJobDescriptionAnalysisSnapshot() throws Exception {
        UserEntity recruiter = getSeededUserEntity("ana.p@techrecruit.com");
        UUID questionId = UUID.fromString("00000001-0000-0000-0000-000000000001");

        CreatePersonalizedAssessmentRequest.JobDescriptionAnalysisSnapshotRequest analysisSnapshot =
                new CreatePersonalizedAssessmentRequest.JobDescriptionAnalysisSnapshotRequest(
                        "Senior backend engineer focused on Java and distributed systems.",
                        Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT, KnowledgeArea.DATABASE),
                        List.of("Java", "Spring Boot"),
                        List.of("Communication", "Ownership"),
                        50,
                        30,
                        20
                );

        CreatePersonalizedAssessmentRequest request = new CreatePersonalizedAssessmentRequest(
                "Weighted Java Developer Test",
                "Assessment for weighted evaluation",
                List.of(questionId),
                LocalDateTime.now().plusDays(7),
                3,
                60,
                45,
                35,
                20,
                analysisSnapshot
        );

        MvcResult result = mockMvc.perform(post("/api/v1/assessments/personalized")
                        .header("X-Dev-User-Email", recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jobDescriptionAnalysis.originalJobDescription")
                        .value("Senior backend engineer focused on Java and distributed systems."))
                .andExpect(jsonPath("$.jobDescriptionAnalysis.suggestedKnowledgeAreas.length()").value(2))
                .andExpect(jsonPath("$.jobDescriptionAnalysis.suggestedHardSkills[0]").value("Java"))
                .andExpect(jsonPath("$.jobDescriptionAnalysis.suggestedSoftSkills[0]").value("Communication"))
                .andExpect(jsonPath("$.jobDescriptionAnalysis.suggestedCriteriaWeights.hardSkillsWeight").value(50))
                .andExpect(jsonPath("$.jobDescriptionAnalysis.suggestedCriteriaWeights.softSkillsWeight").value(30))
                .andExpect(jsonPath("$.jobDescriptionAnalysis.suggestedCriteriaWeights.experienceWeight").value(20))
                .andReturn();

        UUID createdAssessmentId = extractAssessmentId(result);
        PersonalizedAssessmentEntity persistedAssessment = getAssessmentById(createdAssessmentId);

        assertThat(persistedAssessment.getOriginalJobDescription())
                .isEqualTo("Senior backend engineer focused on Java and distributed systems.");
        assertThat(loadSuggestedKnowledgeAreas(createdAssessmentId))
                .containsExactlyInAnyOrder("SOFTWARE_DEVELOPMENT", "DATABASE");
        assertThat(loadSuggestedHardSkills(createdAssessmentId))
                .containsExactly("Java", "Spring Boot");
        assertThat(loadSuggestedSoftSkills(createdAssessmentId))
                .containsExactly("Communication", "Ownership");
        assertThat(persistedAssessment.getSuggestedHardSkillsWeight()).isEqualTo(50);
        assertThat(persistedAssessment.getSuggestedSoftSkillsWeight()).isEqualTo(30);
        assertThat(persistedAssessment.getSuggestedExperienceWeight()).isEqualTo(20);
    }

    @Test
    @DisplayName("Should update personalized assessment criteria weights successfully")
    void shouldUpdatePersonalizedAssessmentCriteriaWeightsSuccessfully() throws Exception {
        UserEntity recruiter = getSeededUserEntity("ana.p@techrecruit.com");
        PersonalizedAssessmentEntity assessment = createTestAssessment(recruiter);

        UpdatePersonalizedAssessmentRequest request = new UpdatePersonalizedAssessmentRequest(
                LocalDateTime.now().plusDays(10),
                5,
                PersonalizedAssessmentStatus.DEACTIVATED,
                25,
                50,
                25
        );

        mockMvc.perform(patch("/api/v1/assessments/personalized/" + assessment.getId())
                        .header("X-Dev-User-Email", recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.criteriaWeights.hardSkillsWeight").value(25))
                .andExpect(jsonPath("$.criteriaWeights.softSkillsWeight").value(50))
                .andExpect(jsonPath("$.criteriaWeights.experienceWeight").value(25))
                .andExpect(jsonPath("$.status").value("DEACTIVATED"));
    }

    @Test
    @DisplayName("Should keep existing job description analysis when updating personalized assessment")
    void shouldKeepExistingJobDescriptionAnalysisWhenUpdatingPersonalizedAssessment() throws Exception {
        UserEntity recruiter = getSeededUserEntity("ana.p@techrecruit.com");
        PersonalizedAssessmentEntity assessment = createTestAssessmentWithAnalysis(recruiter);

        UpdatePersonalizedAssessmentRequest request = new UpdatePersonalizedAssessmentRequest(
                LocalDateTime.now().plusDays(10),
                5,
                PersonalizedAssessmentStatus.DEACTIVATED,
                25,
                50,
                25
        );

        mockMvc.perform(patch("/api/v1/assessments/personalized/" + assessment.getId())
                        .header("X-Dev-User-Email", recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DEACTIVATED"))
                .andExpect(jsonPath("$.jobDescriptionAnalysis.originalJobDescription")
                        .value("Senior backend engineer focused on Java and distributed systems."))
                .andExpect(jsonPath("$.jobDescriptionAnalysis.suggestedHardSkills[0]").value("Java"))
                .andExpect(jsonPath("$.jobDescriptionAnalysis.suggestedCriteriaWeights.hardSkillsWeight").value(50));

        PersonalizedAssessmentEntity updatedAssessment = getAssessmentById(assessment.getId());

        assertThat(updatedAssessment.getStatus()).isEqualTo(PersonalizedAssessmentStatus.DEACTIVATED);
        assertThat(updatedAssessment.getOriginalJobDescription())
                .isEqualTo("Senior backend engineer focused on Java and distributed systems.");
        assertThat(loadSuggestedKnowledgeAreas(assessment.getId()))
                .containsExactlyInAnyOrder("SOFTWARE_DEVELOPMENT", "DATABASE");
        assertThat(loadSuggestedHardSkills(assessment.getId()))
                .containsExactly("Java", "Spring Boot");
        assertThat(loadSuggestedSoftSkills(assessment.getId()))
                .containsExactly("Communication", "Ownership");
        assertThat(updatedAssessment.getSuggestedHardSkillsWeight()).isEqualTo(50);
        assertThat(updatedAssessment.getSuggestedSoftSkillsWeight()).isEqualTo(30);
        assertThat(updatedAssessment.getSuggestedExperienceWeight()).isEqualTo(20);
    }

    private UserEntity getSeededUserEntity(String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Seeded user not found: " + email));
    }

    private PersonalizedAssessmentEntity createTestAssessment(UserEntity recruiter) {
        PersonalizedAssessmentEntity assessment = new PersonalizedAssessmentEntity();
        assessment.setTitle("Test " + UUID.randomUUID());
        assessment.setDescription("desc");
        assessment.setCreationDate(LocalDateTime.now());
        assessment.setCreatedBy((UserRecruiterEntity) recruiter);
        assessment.setShareableToken("token-" + UUID.randomUUID());
        assessment.setStatus(PersonalizedAssessmentStatus.ACTIVE);
        assessment.setMaxAttempts(3);
        assessment.setEvaluationTimerSeconds(1800L);
        return assessmentJpaRepository.save(assessment);
    }

    private PersonalizedAssessmentEntity createTestAssessmentWithAnalysis(UserEntity recruiter) {
        PersonalizedAssessmentEntity assessment = createTestAssessment(recruiter);
        assessment.setOriginalJobDescription("Senior backend engineer focused on Java and distributed systems.");
        assessment.setSuggestedKnowledgeAreas(new HashSet<>(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT, KnowledgeArea.DATABASE)));
        assessment.setSuggestedHardSkills(new ArrayList<>(List.of("Java", "Spring Boot")));
        assessment.setSuggestedSoftSkills(new ArrayList<>(List.of("Communication", "Ownership")));
        assessment.setSuggestedHardSkillsWeight(50);
        assessment.setSuggestedSoftSkillsWeight(30);
        assessment.setSuggestedExperienceWeight(20);
        return assessmentJpaRepository.save(assessment);
    }

    private UUID extractAssessmentId(MvcResult result) throws Exception {
        return UUID.fromString(objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText());
    }

    private PersonalizedAssessmentEntity getAssessmentById(UUID assessmentId) {
        return (PersonalizedAssessmentEntity) assessmentJpaRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found: " + assessmentId));
    }

    private List<String> loadSuggestedKnowledgeAreas(UUID assessmentId) {
        return jdbcTemplate.queryForList(
                "SELECT knowledge_area FROM personalized_assessment_job_description_knowledge_areas WHERE personalized_assessment_id = ?",
                String.class,
                assessmentId
        );
    }

    private List<String> loadSuggestedHardSkills(UUID assessmentId) {
        return jdbcTemplate.queryForList(
                "SELECT skill FROM personalized_assessment_job_description_hard_skills WHERE personalized_assessment_id = ? ORDER BY skill_order",
                String.class,
                assessmentId
        );
    }

    private List<String> loadSuggestedSoftSkills(UUID assessmentId) {
        return jdbcTemplate.queryForList(
                "SELECT skill FROM personalized_assessment_job_description_soft_skills WHERE personalized_assessment_id = ? ORDER BY skill_order",
                String.class,
                assessmentId
        );
    }
}
