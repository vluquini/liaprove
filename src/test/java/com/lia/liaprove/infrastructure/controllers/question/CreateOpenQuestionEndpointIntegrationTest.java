package com.lia.liaprove.infrastructure.controllers.question;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.usecases.question.PreAnalyzeQuestionUseCase;
import com.lia.liaprove.core.usecases.question.PrepareQuestionSubmissionUseCase;
import com.lia.liaprove.infrastructure.dtos.question.CreateOpenQuestionRequest;
import com.lia.liaprove.infrastructure.entities.question.OpenQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
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
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.sql.init.mode=never")
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = "classpath:db/h2-populate-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class CreateOpenQuestionEndpointIntegrationTest {

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
    }

    @Test
    @DisplayName("Should create open question successfully when user is recruiter")
    void shouldCreateOpenQuestionSuccessfullyForRecruiter() throws Exception {
        UserEntity recruiter = QuestionControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                QuestionControllerIntegrationTestSupport.RECRUITER_EMAIL
        );
        CreateOpenQuestionRequest request =
                QuestionControllerIntegrationTestSupport.validOpenQuestionRequest(OpenQuestionVisibility.SHARED);

        mockMvc.perform(post("/api/v1/questions/open")
                        .header(QuestionControllerIntegrationTestSupport.DEV_USER_HEADER, recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type", is("OPEN")))
                .andExpect(jsonPath("$.title", is(request.getTitle())))
                .andExpect(jsonPath("$.guideline", is(request.getGuideline())))
                .andExpect(jsonPath("$.visibility", is(request.getVisibility().name())))
                .andExpect(jsonPath("$.relevanceByLLM", is(RelevanceLevel.THREE.name())));

        QuestionEntity persistedQuestion = questionJpaRepository.findAll().stream()
                .filter(question -> request.getTitle().equals(question.getTitle()))
                .findFirst()
                .orElseThrow();

        assertThat(persistedQuestion).isInstanceOf(OpenQuestionEntity.class);
        OpenQuestionEntity openQuestionEntity = (OpenQuestionEntity) persistedQuestion;
        assertThat(openQuestionEntity.getGuideline()).isEqualTo(request.getGuideline());
        assertThat(openQuestionEntity.getVisibility()).isEqualTo(request.getVisibility());
    }

    @Test
    @DisplayName("Should default open question visibility to private when recruiter omits it")
    void shouldDefaultOpenQuestionVisibilityToPrivateWhenRecruiterOmitsIt() throws Exception {
        UserEntity recruiter = QuestionControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                QuestionControllerIntegrationTestSupport.RECRUITER_EMAIL
        );
        CreateOpenQuestionRequest request = QuestionControllerIntegrationTestSupport.validOpenQuestionRequest(null);
        request.setTitle("Explain the default visibility flow");
        request.setDescription("Describe how recruiters create open questions without explicitly choosing a visibility.");

        mockMvc.perform(post("/api/v1/questions/open")
                        .header(QuestionControllerIntegrationTestSupport.DEV_USER_HEADER, recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type", is("OPEN")))
                .andExpect(jsonPath("$.visibility", is(OpenQuestionVisibility.PRIVATE.name())));

        QuestionEntity persistedQuestion = questionJpaRepository.findAll().stream()
                .filter(question -> request.getTitle().equals(question.getTitle()))
                .findFirst()
                .orElseThrow();

        assertThat(persistedQuestion).isInstanceOf(OpenQuestionEntity.class);
        OpenQuestionEntity openQuestionEntity = (OpenQuestionEntity) persistedQuestion;
        assertThat(openQuestionEntity.getVisibility()).isEqualTo(OpenQuestionVisibility.PRIVATE);
    }

    @Test
    @DisplayName("Should return forbidden when a professional tries to create an open question")
    void shouldReturnForbiddenWhenProfessionalCreatesOpenQuestion() throws Exception {
        UserEntity professional = QuestionControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                QuestionControllerIntegrationTestSupport.PROFESSIONAL_EMAIL
        );
        CreateOpenQuestionRequest request =
                QuestionControllerIntegrationTestSupport.validOpenQuestionRequest(OpenQuestionVisibility.PRIVATE);
        request.setTitle("Explain the forbidden flow");
        request.setDescription("This request should be blocked before it reaches the use case.");

        mockMvc.perform(post("/api/v1/questions/open")
                        .header(QuestionControllerIntegrationTestSupport.DEV_USER_HEADER, professional.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return bad request when open question payload is invalid")
    void shouldReturnBadRequestWhenOpenQuestionPayloadIsInvalid() throws Exception {
        UserEntity recruiter = QuestionControllerIntegrationTestSupport.getSeededUser(
                userJpaRepository,
                QuestionControllerIntegrationTestSupport.RECRUITER_EMAIL
        );
        CreateOpenQuestionRequest invalidRequest = QuestionControllerIntegrationTestSupport.invalidOpenQuestionRequest();

        mockMvc.perform(post("/api/v1/questions/open")
                        .header(QuestionControllerIntegrationTestSupport.DEV_USER_HEADER, recruiter.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.title", notNullValue()))
                .andExpect(jsonPath("$.error.description", notNullValue()))
                .andExpect(jsonPath("$.error.guideline", notNullValue()));
    }

    @Test
    @DisplayName("Should return unauthorized when creating open question without authentication")
    void shouldReturnUnauthorizedWhenCreatingOpenQuestionWithoutAuthentication() throws Exception {
        CreateOpenQuestionRequest request =
                QuestionControllerIntegrationTestSupport.validOpenQuestionRequest(OpenQuestionVisibility.PRIVATE);

        mockMvc.perform(post("/api/v1/questions/open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
