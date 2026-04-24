package com.lia.liaprove.infrastructure.controllers.question;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.usecases.question.PreAnalyzeQuestionUseCase;
import com.lia.liaprove.core.usecases.question.PrepareQuestionSubmissionUseCase;
import com.lia.liaprove.infrastructure.dtos.question.SubmitMultipleChoiceQuestionRequest;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.question.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
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
class PreAnalyzeQuestionEndpointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @MockitoBean
    private PreAnalyzeQuestionUseCase preAnalyzeQuestionUseCase;

    @MockitoBean
    private PrepareQuestionSubmissionUseCase prepareQuestionSubmissionUseCase;

    @AfterEach
    void tearDown() {
        questionJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should pre-analyze question successfully when authenticated")
    void shouldPreAnalyzeQuestionSuccessfully() throws Exception {
        UserEntity user = QuestionControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                QuestionControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        SubmitMultipleChoiceQuestionRequest request = QuestionControllerIntegrationTestSupport.validMultipleChoiceRequest();

        PreAnalyzeQuestionUseCase.PreAnalysisResult mockedResult = new PreAnalyzeQuestionUseCase.PreAnalysisResult(
                List.of("Improve clarity in first sentence."),
                List.of("Potential ambiguity around expected scope."),
                List.of("Distractor suggestion 1"),
                "Intermediate complexity.",
                List.of("Aligned with software development topics.")
        );

        when(preAnalyzeQuestionUseCase.execute(any())).thenReturn(mockedResult);

        mockMvc.perform(post("/api/v1/questions/pre-analysis")
                        .header(QuestionControllerIntegrationTestSupport.DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.languageSuggestions[0]", is("Improve clarity in first sentence.")))
                .andExpect(jsonPath("$.difficultyLevelByLLM", is("Intermediate complexity.")))
                .andExpect(jsonPath("$.distractorSuggestions[0]", is("Distractor suggestion 1")));
    }

    @Test
    @DisplayName("Should return bad request when pre-analysis payload is invalid")
    void shouldReturnBadRequestWhenPreAnalysisPayloadIsInvalid() throws Exception {
        UserEntity user = QuestionControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                QuestionControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        SubmitMultipleChoiceQuestionRequest invalidRequest =
                QuestionControllerIntegrationTestSupport.invalidMultipleChoiceRequest();

        mockMvc.perform(post("/api/v1/questions/pre-analysis")
                        .header(QuestionControllerIntegrationTestSupport.DEV_USER_HEADER, user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.title", notNullValue()))
                .andExpect(jsonPath("$.error.description", notNullValue()));
    }

    @Test
    @DisplayName("Should return unauthorized when pre-analyzing question without authentication")
    void shouldReturnUnauthorizedWhenPreAnalyzingQuestionWithoutAuthentication() throws Exception {
        SubmitMultipleChoiceQuestionRequest request = QuestionControllerIntegrationTestSupport.validMultipleChoiceRequest();

        mockMvc.perform(post("/api/v1/questions/pre-analysis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
