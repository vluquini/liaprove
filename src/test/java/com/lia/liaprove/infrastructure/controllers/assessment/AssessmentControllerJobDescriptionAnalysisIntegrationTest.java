package com.lia.liaprove.infrastructure.controllers.assessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.usecases.assessments.AnalyzeJobDescriptionUseCase;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.sql.init.mode=never")
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = "classpath:db/h2-populate-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AssessmentControllerJobDescriptionAnalysisIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @MockitoBean
    private AnalyzeJobDescriptionUseCase analyzeJobDescriptionUseCase;

    @Test
    void shouldReturnStructuredJobDescriptionAnalysisForRecruiter() throws Exception {
        UserEntity recruiter = userJpaRepository.findByEmail("ana.p@techrecruit.com")
                .orElseThrow(() -> new IllegalStateException("Seeded recruiter not found"));

        when(analyzeJobDescriptionUseCase.execute(anyString())).thenReturn(
                new JobDescriptionAnalysis(
                        "Senior data scientist role focused on predictive analytics.",
                        Set.of(KnowledgeArea.AI, KnowledgeArea.DATABASE),
                        List.of("Python", "Machine learning"),
                        List.of("Communication", "Analytical thinking"),
                        new AssessmentCriteriaWeights(45, 35, 20)
                )
        );

        mockMvc.perform(post("/api/v1/assessments/personalized/job-description-analysis")
                        .header("X-Dev-User-Email", recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                java.util.Map.of(
                                        "jobDescription",
                                        "Senior data scientist role focused on predictive analytics."
                                )
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalJobDescription")
                        .value("Senior data scientist role focused on predictive analytics."))
                .andExpect(jsonPath("$.suggestedKnowledgeAreas[0]").exists())
                .andExpect(jsonPath("$.suggestedHardSkills[0]").value("Python"))
                .andExpect(jsonPath("$.suggestedSoftSkills[0]").value("Communication"))
                .andExpect(jsonPath("$.suggestedCriteriaWeights.hardSkillsWeight").value(45))
                .andExpect(jsonPath("$.suggestedCriteriaWeights.softSkillsWeight").value(35))
                .andExpect(jsonPath("$.suggestedCriteriaWeights.experienceWeight").value(20));
    }
}
