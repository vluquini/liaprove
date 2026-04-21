package com.lia.liaprove.infrastructure.controllers.question;

import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(
        scripts = {"classpath:db/h2-populate-users.sql", "classpath:db/h2-populate-questions.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class GetQuestionVotingDetailsEndpointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @AfterEach
    void tearDown() {
        questionJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should get question voting details successfully when authenticated")
    void shouldGetQuestionVotingDetailsSuccessfully() throws Exception {
        UserEntity user = QuestionControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                "junior.dev@example.com"
        );
        QuestionEntity question = questionJpaRepository.findAll().stream()
                .filter(candidate -> candidate.getStatus() == QuestionStatus.VOTING)
                .findFirst()
                .orElseThrow();

        mockMvc.perform(get("/api/v1/questions/{questionId}/voting-details", question.getId())
                        .header(QuestionControllerIntegrationTestSupport.DEV_USER_HEADER, user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(question.getId().toString())))
                .andExpect(jsonPath("$.title", is(question.getTitle())));
    }

    @Test
    @DisplayName("Should return not found for voting details when question does not exist")
    void shouldReturnNotFoundForVotingDetailsWhenQuestionDoesNotExist() throws Exception {
        UserEntity user = QuestionControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                QuestionControllerIntegrationTestSupport.ADMIN_EMAIL
        );

        mockMvc.perform(get("/api/v1/questions/{questionId}/voting-details", UUID.randomUUID())
                        .header(QuestionControllerIntegrationTestSupport.DEV_USER_HEADER, user.getEmail()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return unauthorized when getting voting details without authentication")
    void shouldReturnUnauthorizedWhenGettingVotingDetailsWithoutAuthentication() throws Exception {
        QuestionEntity question = questionJpaRepository.findAll().stream()
                .filter(candidate -> candidate.getStatus() == QuestionStatus.VOTING)
                .findFirst()
                .orElseThrow();

        mockMvc.perform(get("/api/v1/questions/{questionId}/voting-details", question.getId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
