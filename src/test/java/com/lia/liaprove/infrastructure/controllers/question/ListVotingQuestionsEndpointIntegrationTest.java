package com.lia.liaprove.infrastructure.controllers.question;

import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.question.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
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
class ListVotingQuestionsEndpointIntegrationTest {

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
    @DisplayName("Should list voting questions successfully when authenticated")
    void shouldListVotingQuestionsSuccessfully() throws Exception {
        UserEntity user = QuestionControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                "mariana.costa@example.com"
        );

        mockMvc.perform(get("/api/v1/questions/voting")
                        .header(QuestionControllerIntegrationTestSupport.DEV_USER_HEADER, user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    @DisplayName("Should list voting questions with explicit page and size parameters")
    void shouldListVotingQuestionsWithExplicitPageAndSizeParameters() throws Exception {
        UserEntity user = QuestionControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                "mariana.costa@example.com"
        );

        mockMvc.perform(get("/api/v1/questions/voting")
                        .header(QuestionControllerIntegrationTestSupport.DEV_USER_HEADER, user.getEmail())
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    @DisplayName("Should return unauthorized when listing voting questions without authentication")
    void shouldReturnUnauthorizedWhenListingVotingQuestionsWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/questions/voting"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
