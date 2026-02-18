package com.lia.liaprove.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.infrastructure.dtos.question.AlternativeRequestDto;
import com.lia.liaprove.infrastructure.dtos.question.SubmitMultipleChoiceQuestionRequest;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.users.UserEntity;
import com.lia.liaprove.infrastructure.repositories.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
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

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = {"classpath:db/h2-populate-users.sql", "classpath:db/h2-populate-questions.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class QuestionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @AfterEach
    void tearDown() {
        questionJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    private UserEntity getSeededUserEntity(String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Seeded user not found: " + email));
    }

    @Test
    @DisplayName("Should submit question successfully when authenticated")
    void shouldSubmitQuestionSuccessfully() throws Exception {
        // Setup
        UserEntity user = getSeededUserEntity("carlos.silva@example.com");

        SubmitMultipleChoiceQuestionRequest request = new SubmitMultipleChoiceQuestionRequest();
        request.setTitle("New Question Title with minimum length");
        request.setDescription("New question description that meets the minimum length requirement.");
        request.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        request.setDifficultyByCommunity(DifficultyLevel.EASY);
        request.setRelevanceByCommunity(RelevanceLevel.THREE);

        request.setAlternatives(List.of(
                new AlternativeRequestDto("Correct Answer Text", true),
                new AlternativeRequestDto("Wrong Answer Text 1", false),
                new AlternativeRequestDto("Wrong Answer Text 2", false)
        ));

        // Act
        mockMvc.perform(post("/api/v1/questions")
                        .header("X-Dev-User-Email", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is(request.getTitle())))
                .andExpect(jsonPath("$.status", is(QuestionStatus.VOTING.name())));

        // Assert
        List<QuestionEntity> questions = questionJpaRepository.findAll();
        assertThat(questions).anyMatch(q -> request.getTitle().equals(q.getTitle()));
    }

    @Test
    @DisplayName("Should list voting questions successfully when authenticated")
    void shouldListVotingQuestionsSuccessfully() throws Exception {
        // Setup
        UserEntity user = getSeededUserEntity("mariana.costa@example.com");
        long votingCount = questionJpaRepository.findAll().stream()
                .filter(q -> q.getStatus() == QuestionStatus.VOTING)
                .count();

        // Act & Assert
        mockMvc.perform(get("/api/v1/questions/voting")
                        .header("X-Dev-User-Email", user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize((int) votingCount)));
                // Removido check de status pois QuestionSummaryResponse não possui este campo
    }

    @Test
    @DisplayName("Should return unauthorized when listing voting questions without authentication")
    void shouldReturnUnauthorizedWhenListingVotingQuestions() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/questions/voting"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should get question voting details successfully when authenticated")
    void shouldGetQuestionVotingDetailsSuccessfully() throws Exception {
        // Setup
        UserEntity user = getSeededUserEntity("junior.dev@example.com");
        QuestionEntity question = questionJpaRepository.findAll().stream()
                .filter(q -> q.getStatus() == QuestionStatus.VOTING)
                .findFirst()
                .orElseThrow();

        // Act & Assert
        mockMvc.perform(get("/api/v1/questions/{questionId}/voting-details", question.getId())
                        .header("X-Dev-User-Email", user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(question.getId().toString())))
                .andExpect(jsonPath("$.title", is(question.getTitle())));
                // Ajustado paths JSON pois QuestionDetailResponse é flat na raiz
    }

    @Test
    @DisplayName("Should return Not Found for voting details when question does not exist")
    void shouldReturnNotFoundForVotingDetails() throws Exception {
        // Setup
        UserEntity user = getSeededUserEntity("admin@liaprove.com");
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(get("/api/v1/questions/{questionId}/voting-details", nonExistentId)
                        .header("X-Dev-User-Email", user.getEmail()))
                .andExpect(status().isNotFound());
    }
}
