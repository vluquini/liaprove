package com.lia.liaprove.infrastructure.controllers.question;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.usecases.question.PrepareQuestionSubmissionUseCase;
import com.lia.liaprove.infrastructure.dtos.question.SubmitMultipleChoiceQuestionRequest;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(
        scripts = {"classpath:db/h2-populate-users.sql", "classpath:db/h2-populate-questions.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class SubmitQuestionEndpointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @MockitoBean
    private PrepareQuestionSubmissionUseCase prepareQuestionSubmissionUseCase;

    @AfterEach
    void tearDown() {
        questionJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should submit question successfully when authenticated")
    void shouldSubmitQuestionSuccessfully() throws Exception {
        UserEntity user = QuestionControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                QuestionControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        SubmitMultipleChoiceQuestionRequest request = QuestionControllerIntegrationTestSupport.validMultipleChoiceRequest();

        when(prepareQuestionSubmissionUseCase.execute(any())).thenReturn(
                new PrepareQuestionSubmissionUseCase.PreparedQuestion(
                        request.getTitle(),
                        request.getDescription(),
                        List.of(
                                new PrepareQuestionSubmissionUseCase.AlternativeInput("Correct Answer Text", true),
                                new PrepareQuestionSubmissionUseCase.AlternativeInput("Wrong Answer Text 1", false),
                                new PrepareQuestionSubmissionUseCase.AlternativeInput("Wrong Answer Text 2", false)
                        ),
                        RelevanceLevel.FOUR
                )
        );

        mockMvc.perform(post("/api/v1/questions")
                        .header(QuestionControllerIntegrationTestSupport.DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is(request.getTitle())))
                .andExpect(jsonPath("$.status", is(QuestionStatus.VOTING.name())));

        List<QuestionEntity> questions = questionJpaRepository.findAll();
        assertThat(questions).anyMatch(question -> request.getTitle().equals(question.getTitle()));
    }

    @Test
    @DisplayName("Should return bad request when submit payload is invalid")
    void shouldReturnBadRequestWhenSubmitPayloadIsInvalid() throws Exception {
        UserEntity user = QuestionControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                QuestionControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        SubmitMultipleChoiceQuestionRequest invalidRequest =
                QuestionControllerIntegrationTestSupport.invalidMultipleChoiceRequest();

        mockMvc.perform(post("/api/v1/questions")
                        .header(QuestionControllerIntegrationTestSupport.DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.title", notNullValue()))
                .andExpect(jsonPath("$.error.description", notNullValue()));
    }

    @Test
    @DisplayName("Should return unauthorized when submitting question without authentication")
    void shouldReturnUnauthorizedWhenSubmittingQuestionWithoutAuthentication() throws Exception {
        SubmitMultipleChoiceQuestionRequest request = QuestionControllerIntegrationTestSupport.validMultipleChoiceRequest();

        mockMvc.perform(post("/api/v1/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
