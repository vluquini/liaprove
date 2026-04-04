package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import com.lia.liaprove.infrastructure.repositories.FeedbackQuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.VoteJpaRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = {"classpath:db/h2-populate-users.sql", "classpath:db/h2-populate-questions.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AdminMetricsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @Autowired
    private FeedbackQuestionJpaRepository feedbackQuestionJpaRepository;

    @Autowired
    private VoteJpaRepository voteJpaRepository;

    @AfterEach
    void tearDown() {
        voteJpaRepository.deleteAll();
        feedbackQuestionJpaRepository.deleteAll();
        questionJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    private UserEntity getSeededUserEntity(String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Seeded user not found: " + email));
    }

    @Test
    @DisplayName("Should list votes for question as admin")
    void shouldListVotesForQuestionAsAdmin() throws Exception {
        UserEntity admin = getSeededUserEntity("admin@liaprove.com");
        QuestionEntity question = questionJpaRepository.findAll().getFirst(); // Assuming at least one question exists

        mockMvc.perform(get("/api/v1/admin/metrics/questions/{questionId}/votes", question.getId())
                        .header("X-Dev-User-Email", admin.getEmail()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should list feedbacks for question as admin")
    void shouldListFeedbacksForQuestionAsAdmin() throws Exception {
        UserEntity admin = getSeededUserEntity("admin@liaprove.com");
        QuestionEntity question = questionJpaRepository.findAll().getFirst(); // Assuming at least one question exists

        mockMvc.perform(get("/api/v1/admin/metrics/questions/{questionId}/feedbacks", question.getId())
                        .header("X-Dev-User-Email", admin.getEmail()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return forbidden when non-admin accesses admin metrics endpoints")
    void shouldReturnForbiddenForNonAdmin() throws Exception {
        UserEntity user = getSeededUserEntity("carlos.silva@example.com");

        mockMvc.perform(get("/api/v1/admin/metrics/questions/{questionId}/votes", UUID.randomUUID())
                        .header("X-Dev-User-Email", user.getEmail()))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/admin/metrics/questions/{questionId}/feedbacks", UUID.randomUUID())
                        .header("X-Dev-User-Email", user.getEmail()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return not found if question does not exist for votes")
    void shouldReturnNotFoundForNonExistentQuestionVotes() throws Exception {
        UserEntity admin = getSeededUserEntity("admin@liaprove.com");
        mockMvc.perform(get("/api/v1/admin/metrics/questions/{questionId}/votes", UUID.randomUUID())
                        .header("X-Dev-User-Email", admin.getEmail()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return not found if question does not exist for feedbacks")
    void shouldReturnNotFoundForNonExistentQuestionFeedbacks() throws Exception {
        UserEntity admin = getSeededUserEntity("admin@liaprove.com");
        mockMvc.perform(get("/api/v1/admin/metrics/questions/{questionId}/feedbacks", UUID.randomUUID())
                        .header("X-Dev-User-Email", admin.getEmail()))
                .andExpect(status().isNotFound());
    }
}
