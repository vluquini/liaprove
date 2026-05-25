package com.lia.liaprove.infrastructure.controllers.question;

import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.question.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
import com.lia.liaprove.infrastructure.repositories.metrics.FeedbackQuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.metrics.VoteJpaRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = {"classpath:db/h2-populate-users.sql", "classpath:db/h2-populate-questions.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AdminQuestionControllerIntegrationTest {

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
    @DisplayName("Should return forbidden when non-admin accesses admin question endpoints")
    void shouldReturnForbiddenForNonAdminQuestionEndpoints() throws Exception {
        UserEntity user = getSeededUserEntity("carlos.silva@example.com");

        // Test access to other admin question endpoints that remain here
        mockMvc.perform(get("/api/v1/admin/questions")
                        .header("X-Dev-User-Email", user.getEmail()))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/admin/questions/{id}", UUID.randomUUID())
                        .header("X-Dev-User-Email", user.getEmail()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should filter admin questions by author name")
    void shouldFilterAdminQuestionsByAuthorName() throws Exception {
        UserEntity admin = getSeededUserEntity("admin@liaprove.com");

        mockMvc.perform(get("/api/v1/admin/questions")
                        .param("authorName", "Ana Pereira")
                        .param("size", "20")
                        .header("X-Dev-User-Email", admin.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[*].authorId", everyItem(is("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15"))));
    }
}
