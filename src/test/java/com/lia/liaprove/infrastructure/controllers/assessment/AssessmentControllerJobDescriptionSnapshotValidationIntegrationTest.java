package com.lia.liaprove.infrastructure.controllers.assessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.infrastructure.dtos.assessment.CreatePersonalizedAssessmentRequest;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
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
import java.util.UUID;

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
class AssessmentControllerJobDescriptionSnapshotValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    void shouldReturnBadRequestWhenJobDescriptionSnapshotWeightsArePartial() throws Exception {
        UserEntity recruiter = userJpaRepository.findByEmail("ana.p@techrecruit.com")
                .orElseThrow(() -> new IllegalStateException("Seeded recruiter not found"));

        CreatePersonalizedAssessmentRequest request = new CreatePersonalizedAssessmentRequest(
                "Weighted Java Developer Test",
                "Assessment for weighted evaluation",
                List.of(UUID.fromString("00000001-0000-0000-0000-000000000001")),
                LocalDateTime.now().plusDays(7),
                3,
                60,
                45,
                35,
                20,
                new CreatePersonalizedAssessmentRequest.JobDescriptionAnalysisSnapshotRequest(
                        "Senior backend engineer focused on Java and distributed systems.",
                        null,
                        List.of("Java"),
                        List.of("Communication"),
                        50,
                        null,
                        20
                )
        );

        mockMvc.perform(post("/api/v1/assessments/personalized")
                        .header("X-Dev-User-Email", recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error['jobDescriptionAnalysis.suggestedWeightsValid']")
                        .value("Suggested snapshot weights must be either fully omitted or sum to 100."));
    }
}
