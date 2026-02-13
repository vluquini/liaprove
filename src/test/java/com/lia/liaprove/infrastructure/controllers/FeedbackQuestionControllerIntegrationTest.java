package com.lia.liaprove.infrastructure.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.metrics.ReactionType;
import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.infrastructure.dtos.metrics.ReactToFeedbackRequest;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.users.UserEntity;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.repositories.FeedbackQuestionJpaRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev") // Ensure dev profile is active for H2 config and DevAuthenticationFilter
@Sql(scripts = {"classpath:db/h2-populate-users.sql", "classpath:db/h2-populate-questions.sql"},
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class FeedbackQuestionControllerIntegrationTest {

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
    private QuestionMapper questionMapper;

    @AfterEach
    void tearDown() {
        feedbackQuestionJpaRepository.deleteAll();
        questionJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    // New utility method to get a seeded user entity
    private UserEntity getSeededUserEntity(String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Seeded user not found: " + email));
    }

    private QuestionEntity createTestQuestion() {
        MultipleChoiceQuestion questionDomain = new MultipleChoiceQuestion(
                List.of(
                        new Alternative(null, "Alternative A", false),
                        new Alternative(null, "Alternative B", true)
                )
        );
        questionDomain.setAuthorId(UUID.randomUUID());
        questionDomain.setTitle("Test Question Title - " + UUID.randomUUID()); // Ensure unique title
        questionDomain.setDescription("Test question description. - " + UUID.randomUUID()); // Ensure unique description
        questionDomain.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        questionDomain.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        questionDomain.setStatus(QuestionStatus.VOTING);
        questionDomain.setSubmissionDate(LocalDateTime.now());
        questionDomain.setVotingEndDate(LocalDateTime.now().plusDays(7));
        questionDomain.setRelevanceByCommunity(RelevanceLevel.THREE);
        questionDomain.setRelevanceByLLM(RelevanceLevel.FOUR);
        questionDomain.setRecruiterUsageCount(0);


        QuestionEntity entity = questionMapper.toEntity(questionDomain);
        return questionJpaRepository.save(entity);
    }

    private FeedbackQuestionEntity createTestFeedbackQuestion(UserEntity author, QuestionEntity question) {
        FeedbackQuestionEntity feedback = new FeedbackQuestionEntity();
        feedback.setUser(author);
        feedback.setQuestion(question);
        feedback.setComment("This is a test feedback comment.");
        feedback.setSubmissionDate(LocalDateTime.now());
        return feedbackQuestionJpaRepository.save(feedback);
    }

    @Test
    @DisplayName("Should react to feedback successfully with LIKE")
    void shouldReactToFeedbackSuccessfullyWithLike() throws Exception {
        // Setup
        UserEntity author = getSeededUserEntity("carlos.silva@example.com");
        UserEntity reactor = getSeededUserEntity("mariana.costa@example.com");

        QuestionEntity question = createTestQuestion();
        FeedbackQuestionEntity feedback = createTestFeedbackQuestion(author, question);

        ReactToFeedbackRequest reactRequest = new ReactToFeedbackRequest();
        reactRequest.setReactionType(ReactionType.LIKE);

        // Act
        mockMvc.perform(post("/api/v1/questions/feedbacks/{feedbackId}/react", feedback.getId())
                        .header("X-Dev-User-Email", reactor.getEmail()) // Authenticate via DevAuthenticationFilter
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reactRequest)))
                .andExpect(status().isCreated());

        // Assert
        FeedbackQuestionEntity updatedFeedback = feedbackQuestionJpaRepository.findFeedbackByIdWithDetails(feedback.getId()).orElseThrow();
        assertThat(updatedFeedback.getReactions()).hasSize(1);
        assertThat(updatedFeedback.getReactions().get(0).getUser().getId()).isEqualTo(reactor.getId());
        assertThat(updatedFeedback.getReactions().get(0).getType()).isEqualTo(ReactionType.LIKE);
    }

    @Test
    @DisplayName("Should update reaction successfully from LIKE to DISLIKE")
    void shouldUpdateReactionSuccessfullyFromLikeToDislike() throws Exception {
        // Setup
        UserEntity author = getSeededUserEntity("mariana.costa@example.com");
        UserEntity reactor = getSeededUserEntity("junior.dev@example.com");

        QuestionEntity question = createTestQuestion();
        FeedbackQuestionEntity feedback = createTestFeedbackQuestion(author, question);

        // First reaction: LIKE
        ReactToFeedbackRequest likeRequest = new ReactToFeedbackRequest();
        likeRequest.setReactionType(ReactionType.LIKE);
        mockMvc.perform(post("/api/v1/questions/feedbacks/{feedbackId}/react", feedback.getId())
                        .header("X-Dev-User-Email", reactor.getEmail()) // Authenticate via DevAuthenticationFilter
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeRequest)))
                .andExpect(status().isCreated());

        // Second reaction: DISLIKE (update)
        ReactToFeedbackRequest dislikeRequest = new ReactToFeedbackRequest();
        dislikeRequest.setReactionType(ReactionType.DISLIKE);
        mockMvc.perform(post("/api/v1/questions/feedbacks/{feedbackId}/react", feedback.getId())
                        .header("X-Dev-User-Email", reactor.getEmail()) // Authenticate via DevAuthenticationFilter
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dislikeRequest)))
                .andExpect(status().isCreated());

        // Assert
        FeedbackQuestionEntity updatedFeedback = feedbackQuestionJpaRepository.findFeedbackByIdWithDetails(feedback.getId()).orElseThrow();
        assertThat(updatedFeedback.getReactions()).hasSize(1); // Should still be one reaction from the user
        assertThat(updatedFeedback.getReactions().get(0).getUser().getId()).isEqualTo(reactor.getId());
        assertThat(updatedFeedback.getReactions().get(0).getType()).isEqualTo(ReactionType.DISLIKE);
    }

    @Test
    @DisplayName("Should remove reaction when same type submitted again (LIKE -> LIKE)")
    void shouldRemoveReactionWhenSameTypeSubmittedAgain() throws Exception {
        // Setup
        UserEntity author = getSeededUserEntity("junior.dev@example.com");
        UserEntity reactor = getSeededUserEntity("carlos.silva@example.com");

        QuestionEntity question = createTestQuestion();
        FeedbackQuestionEntity feedback = createTestFeedbackQuestion(author, question);

        // First reaction: LIKE
        ReactToFeedbackRequest likeRequest1 = new ReactToFeedbackRequest();
        likeRequest1.setReactionType(ReactionType.LIKE);
        mockMvc.perform(post("/api/v1/questions/feedbacks/{feedbackId}/react", feedback.getId())
                        .header("X-Dev-User-Email", reactor.getEmail()) // Authenticate via DevAuthenticationFilter
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeRequest1)))
                .andExpect(status().isCreated());

        // Verify reaction was added
        FeedbackQuestionEntity feedbackAfterFirstReact = feedbackQuestionJpaRepository.findFeedbackByIdWithDetails(feedback.getId()).orElseThrow();
        assertThat(feedbackAfterFirstReact.getReactions()).hasSize(1);

        // Second reaction: LIKE (should remove)
        ReactToFeedbackRequest likeRequest2 = new ReactToFeedbackRequest();
        likeRequest2.setReactionType(ReactionType.LIKE);
        mockMvc.perform(post("/api/v1/questions/feedbacks/{feedbackId}/react", feedback.getId())
                        .header("X-Dev-User-Email", reactor.getEmail()) // Authenticate via DevAuthenticationFilter
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeRequest2)))
                .andExpect(status().isCreated());

        // Assert reaction was removed
        FeedbackQuestionEntity updatedFeedback = feedbackQuestionJpaRepository.findFeedbackByIdWithDetails(feedback.getId()).orElseThrow();
        assertThat(updatedFeedback.getReactions()).isEmpty();
    }

    @Test
    @DisplayName("Should return FORBIDDEN when reacting to own feedback")
    void shouldReturnForbiddenWhenReactingToOwnFeedback() throws Exception {
        // Setup
        UserEntity feedbackAuthor = getSeededUserEntity("admin@liaprove.com"); // Using seeded admin user

        QuestionEntity question = createTestQuestion();
        // Feedback created by the same user who will try to react
        FeedbackQuestionEntity feedback = createTestFeedbackQuestion(feedbackAuthor, question);

        ReactToFeedbackRequest reactRequest = new ReactToFeedbackRequest();
        reactRequest.setReactionType(ReactionType.LIKE);

        // Act & Assert
        mockMvc.perform(post("/api/v1/questions/feedbacks/{feedbackId}/react", feedback.getId())
                        .header("X-Dev-User-Email", feedbackAuthor.getEmail()) // Authenticate via DevAuthenticationFilter
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reactRequest)))
                .andExpect(status().isForbidden()); // Expect 403 Forbidden
    }

    @Test
    @DisplayName("Should return NOT_FOUND when feedback does not exist")
    void shouldReturnNotFoundWhenFeedbackDoesNotExist() throws Exception {
        // Setup
        UserEntity user = getSeededUserEntity("carlos.silva@example.com"); // Using seeded professional user

        UUID nonExistentFeedbackId = UUID.randomUUID();

        ReactToFeedbackRequest reactRequest = new ReactToFeedbackRequest();
        reactRequest.setReactionType(ReactionType.LIKE);

        // Act & Assert
        mockMvc.perform(post("/api/v1/questions/feedbacks/{feedbackId}/react", nonExistentFeedbackId)
                        .header("X-Dev-User-Email", user.getEmail()) // Authenticate via DevAuthenticationFilter
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reactRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return UNAUTHORIZED when reacting to feedback without authentication")
    void shouldReturnUnauthorizedWhenReactingToFeedbackWithoutAuthentication() throws Exception {
        // Setup
        // No token or dev email provided for this test case (UNAUTHORIZED)
        UserEntity user = getSeededUserEntity("carlos.silva@example.com"); // Get a seeded user for the feedback author
        QuestionEntity question = createTestQuestion();
        FeedbackQuestionEntity feedback = createTestFeedbackQuestion(user, question);

        ReactToFeedbackRequest reactRequest = new ReactToFeedbackRequest();
        reactRequest.setReactionType(ReactionType.LIKE);

        // Act & Assert
        mockMvc.perform(post("/api/v1/questions/feedbacks/{feedbackId}/react", feedback.getId())
                        // No authentication header here
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reactRequest)))
                .andExpect(status().isUnauthorized());
    }
}