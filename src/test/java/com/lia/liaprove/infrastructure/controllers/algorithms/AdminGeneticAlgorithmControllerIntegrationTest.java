package com.lia.liaprove.infrastructure.controllers.algorithms;

import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.metrics.VoteMultiplierJpaRepository;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = {"classpath:db/h2-populate-users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AdminGeneticAlgorithmControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private VoteMultiplierJpaRepository voteMultiplierJpaRepository;

    @AfterEach
    void tearDown() {
        voteMultiplierJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should list recruiter vote weights with individual multiplier overrides")
    void shouldListRecruiterVoteWeightsWithMultiplierOverrides() throws Exception {
        UserEntity admin = getSeededUser("admin@liaprove.com");
        UserEntity recruiter = getSeededUser("ana.p@techrecruit.com");

        mockMvc.perform(patch("/api/v1/admin/algorithms/genetic/recruiters/{id}/multiplier", recruiter.getId())
                        .header("X-Dev-User-Email", admin.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"multiplier\":1.75}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/admin/algorithms/genetic/recruiters/weights")
                        .header("X-Dev-User-Email", admin.getEmail())
                        .param("name", "Ana")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(recruiter.getId().toString()))
                .andExpect(jsonPath("$[0].name").value("Ana Pereira"))
                .andExpect(jsonPath("$[0].email").value("ana.p@techrecruit.com"))
                .andExpect(jsonPath("$[0].companyName").value("TechRecruit"))
                .andExpect(jsonPath("$[0].companyEmail").value("contact@techrecruit.com"))
                .andExpect(jsonPath("$[0].voteWeight").value(10))
                .andExpect(jsonPath("$[0].multiplier").value(1.75));
    }

    @Test
    @DisplayName("Should list only recruiters and keep multiplier null when no override exists")
    void shouldListOnlyRecruitersWithNullMultiplierWhenNoOverrideExists() throws Exception {
        UserEntity admin = getSeededUser("admin@liaprove.com");

        mockMvc.perform(get("/api/v1/admin/algorithms/genetic/recruiters/weights")
                        .header("X-Dev-User-Email", admin.getEmail())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Ana Pereira"))
                .andExpect(jsonPath("$[0].voteWeight").value(10))
                .andExpect(jsonPath("$[0].multiplier", nullValue()))
                .andExpect(jsonPath("$[1].name").value("Roberto Lima"))
                .andExpect(jsonPath("$[1].voteWeight").value(5))
                .andExpect(jsonPath("$[1].multiplier", nullValue()));
    }

    @Test
    @DisplayName("Should update recruiter vote weights in batch")
    void shouldUpdateRecruiterVoteWeightsInBatch() throws Exception {
        UserEntity admin = getSeededUser("admin@liaprove.com");
        UserEntity recruiter = getSeededUser("roberto.l@hiredev.com");

        mockMvc.perform(patch("/api/v1/admin/algorithms/genetic/recruiters/vote-weights")
                        .header("X-Dev-User-Email", admin.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"weights":[{"recruiterId":"%s","weight":9}]}
                                """.formatted(recruiter.getId())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/admin/algorithms/genetic/recruiters/weights")
                        .header("X-Dev-User-Email", admin.getEmail())
                        .param("name", "Roberto")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].voteWeight").value(9));
    }

    @Test
    @DisplayName("Should return forbidden when non-admin lists recruiter vote weights")
    void shouldReturnForbiddenWhenNonAdminListsRecruiterVoteWeights() throws Exception {
        UserEntity professional = getSeededUser("carlos.silva@example.com");

        mockMvc.perform(get("/api/v1/admin/algorithms/genetic/recruiters/weights")
                        .header("X-Dev-User-Email", professional.getEmail()))
                .andExpect(status().isForbidden());
    }

    private UserEntity getSeededUser(String email) {
        return userJpaRepository.findByEmail(email).orElseThrow();
    }
}
