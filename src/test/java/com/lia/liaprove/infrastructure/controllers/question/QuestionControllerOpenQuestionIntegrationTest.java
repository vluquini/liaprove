package com.lia.liaprove.infrastructure.controllers.question;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.usecases.question.PreAnalyzeQuestionUseCase;
import com.lia.liaprove.core.usecases.question.PrepareQuestionSubmissionUseCase;
import com.lia.liaprove.application.gateways.ai.QuestionPreAnalysisGateway;
import com.lia.liaprove.infrastructure.dtos.question.CreateOpenQuestionRequest;
import com.lia.liaprove.infrastructure.entities.question.OpenQuestionEntity;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.sql.init.mode=never")
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = "classpath:db/h2-populate-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class QuestionControllerOpenQuestionIntegrationTest {

    private static final String RECRUITER_EMAIL = "ana.p@techrecruit.com";
    private static final String PROFESSIONAL_EMAIL = "carlos.silva@example.com";

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

    @MockitoBean
    private QuestionPreAnalysisGateway questionPreAnalysisGateway;

    @AfterEach
    void tearDown() {
        questionJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create open question successfully when user is Recruiter")
    void shouldCreateOpenQuestionSuccessfullyForRecruiter() throws Exception {
        UserEntity recruiter = getSeededUserEntity(RECRUITER_EMAIL);

        CreateOpenQuestionRequest request = openQuestionRequest(
                "Explain the open question creation flow",
                "Describe how recruiters create open questions and how they are persisted.",
                OpenQuestionVisibility.SHARED
        );

        mockMvc.perform(post("/api/v1/questions/open")
                        .header("X-Dev-User-Email", recruiter.getEmail())
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
    @DisplayName("Should default open question visibility to PRIVATE when recruiter omits it")
    void shouldDefaultOpenQuestionVisibilityToPrivateWhenRecruiterOmitsIt() throws Exception {
        UserEntity recruiter = getSeededUserEntity(RECRUITER_EMAIL);

        CreateOpenQuestionRequest request = openQuestionRequest(
                "Explain the default visibility flow",
                "Describe how recruiters create open questions without explicitly choosing a visibility.",
                null
        );

        mockMvc.perform(post("/api/v1/questions/open")
                        .header("X-Dev-User-Email", recruiter.getEmail())
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
        UserEntity professional = getSeededUserEntity(PROFESSIONAL_EMAIL);

        CreateOpenQuestionRequest request = openQuestionRequest(
                "Explain the forbidden flow",
                "This request should be blocked before it reaches the use case.",
                OpenQuestionVisibility.PRIVATE
        );

        mockMvc.perform(post("/api/v1/questions/open")
                        .header("X-Dev-User-Email", professional.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    private UserEntity getSeededUserEntity(String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Seeded user not found: " + email));
    }

    private CreateOpenQuestionRequest openQuestionRequest(String title, String description, OpenQuestionVisibility visibility) {
        CreateOpenQuestionRequest request = new CreateOpenQuestionRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        request.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        request.setRelevanceByCommunity(RelevanceLevel.FOUR);
        request.setGuideline("Mention the expected answer structure and scoring hints.");
        request.setVisibility(visibility);
        return request;
    }
}
