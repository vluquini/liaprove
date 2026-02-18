package com.lia.liaprove.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.infrastructure.dtos.question.ModerateQuestionRequest;
import com.lia.liaprove.infrastructure.dtos.question.UpdateQuestionRequest;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.users.UserEntity;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = {"classpath:db/h2-populate-users.sql", "classpath:db/h2-populate-questions.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AdminQuestionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("Should list all questions for admin")
    void shouldListAllQuestionsForAdmin() throws Exception {
        UserEntity admin = getSeededUserEntity("admin@liaprove.com");
        long totalQuestions = questionJpaRepository.count();

        mockMvc.perform(get("/api/v1/admin/questions")
                        .header("X-Dev-User-Email", admin.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize((int) totalQuestions)));
    }

    @Test
    @DisplayName("Should get question by ID for admin")
    void shouldGetQuestionByIdForAdmin() throws Exception {
        UserEntity admin = getSeededUserEntity("admin@liaprove.com");
        QuestionEntity question = questionJpaRepository.findAll().get(0);

        mockMvc.perform(get("/api/v1/admin/questions/{id}", question.getId())
                        .header("X-Dev-User-Email", admin.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(question.getId().toString())));
    }

    @Test
    @DisplayName("Should update question successfully as admin")
    void shouldUpdateQuestionSuccessfullyAsAdmin() throws Exception {
        UserEntity admin = getSeededUserEntity("admin@liaprove.com");
        QuestionEntity question = questionJpaRepository.findAll().get(0);

        UpdateQuestionRequest request = new UpdateQuestionRequest(
                "Updated Title Admin",
                "Updated description admin.",
                Set.of(KnowledgeArea.AI),
                null // keep existing alternatives for simplicity in this mock test
        );

        mockMvc.perform(put("/api/v1/admin/questions/{id}", question.getId())
                        .header("X-Dev-User-Email", admin.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title Admin")));
    }

    @Test
    @DisplayName("Should moderate question successfully as admin")
    void shouldModerateQuestionSuccessfullyAsAdmin() throws Exception {
        UserEntity admin = getSeededUserEntity("admin@liaprove.com");
        QuestionEntity question = questionJpaRepository.findAll().stream()
                .filter(q -> q.getStatus() == QuestionStatus.VOTING)
                .findFirst().orElseThrow();

        ModerateQuestionRequest request = new ModerateQuestionRequest(QuestionStatus.APPROVED);

        mockMvc.perform(patch("/api/v1/admin/questions/{id}/moderate", question.getId())
                        .header("X-Dev-User-Email", admin.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(QuestionStatus.APPROVED.name())));

        QuestionEntity updated = questionJpaRepository.findById(question.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(QuestionStatus.APPROVED);
    }

    @Test
    @DisplayName("Should list votes for question as admin")
    void shouldListVotesForQuestionAsAdmin() throws Exception {
        UserEntity admin = getSeededUserEntity("admin@liaprove.com");
        QuestionEntity question = questionJpaRepository.findAll().get(0);

        mockMvc.perform(get("/api/v1/admin/questions/{id}/votes", question.getId())
                        .header("X-Dev-User-Email", admin.getEmail()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should list feedbacks for question as admin")
    void shouldListFeedbacksForQuestionAsAdmin() throws Exception {
        UserEntity admin = getSeededUserEntity("admin@liaprove.com");
        QuestionEntity question = questionJpaRepository.findAll().get(0);

        mockMvc.perform(get("/api/v1/admin/questions/{id}/feedbacks", question.getId())
                        .header("X-Dev-User-Email", admin.getEmail()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return forbidden when non-admin accesses admin endpoints")
    void shouldReturnForbiddenForNonAdmin() throws Exception {
        UserEntity user = getSeededUserEntity("carlos.silva@example.com");

        mockMvc.perform(get("/api/v1/admin/questions")
                        .header("X-Dev-User-Email", user.getEmail()))
                .andExpect(status().isForbidden());
    }
}
