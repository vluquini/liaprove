package com.lia.liaprove.infrastructure.services.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.application.gateways.ai.AttemptPreAnalysisContext;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.AttemptPreAnalysis;
import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;
import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.OpenQuestion;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserProfessional;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.domain.user.UserStatus;
import com.lia.liaprove.core.usecases.question.PreAnalyzeQuestionUseCase;
import com.lia.liaprove.infrastructure.dtos.ai.LlmAttemptPreAnalysisOutput;
import com.lia.liaprove.infrastructure.dtos.ai.LlmJobDescriptionAnalysisOutput;
import com.lia.liaprove.infrastructure.dtos.ai.LlmPreAnalysisOutput;
import com.lia.liaprove.infrastructure.dtos.ai.ProviderChatRequest;
import com.lia.liaprove.infrastructure.dtos.ai.ProviderChatResponse;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpQuestionPreAnalysisGatewayImplTest {

    private static final String TEST_MODEL = "gpt-test";
    private static final String QUESTION_PRE_ANALYSIS_SYSTEM_PROMPT = "Question pre-analysis prompt";
    private static final String ATTEMPT_PRE_ANALYSIS_SYSTEM_PROMPT = "Attempt pre-analysis prompt";

    @Test
    void shouldFallbackToDefaultWeightsWhenProviderReturnsInvalidTotal() throws Exception {
        LlmJobDescriptionAnalysisOutput output = new LlmJobDescriptionAnalysisOutput(
                "Senior backend engineer",
                List.of("SOFTWARE_DEVELOPMENT"),
                List.of("Java"),
                List.of("Communication"),
                50,
                30,
                10
        );

        AssessmentCriteriaWeights weights = invokeResolveSuggestedWeights(output);

        assertThat(weights).isEqualTo(AssessmentCriteriaWeights.defaultWeights());
    }

    @Test
    void shouldAnalyzeAttemptAndReturnStructuredPreAnalysis() throws Exception {
        AttemptPreAnalysisContext context = buildAttemptContext();
        String llmContent = new ObjectMapper().writeValueAsString(new LlmAttemptPreAnalysisOutput(
                "Candidate shows solid technical reasoning.",
                List.of("Clear problem decomposition", "Good trade-off awareness"),
                List.of("Should improve API validation"),
                "Overall, this attempt is a strong fit for recruiter review."
        ));

        AttemptPreAnalysis.Analysis analysis = executeGateway(context, llmContent, payload -> {
            assertThat(payload.at("/attemptId").asText()).isEqualTo("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
            assertThat(payload.at("/attemptStatus").asText()).isEqualTo("COMPLETED");
            assertThat(payload.at("/accuracyRate").asInt()).isEqualTo(83);
            assertThat(payload.at("/assessment/title").asText()).isEqualTo("Personalized");
            assertThat(payload.at("/assessment/description").asText()).isEqualTo("Personalized assessment");
            assertThat(payload.at("/assessment/criteriaWeights/hardSkillsWeight").asInt()).isEqualTo(50);
            assertThat(payload.at("/assessment/criteriaWeights/softSkillsWeight").asInt()).isEqualTo(30);
            assertThat(payload.at("/assessment/criteriaWeights/experienceWeight").asInt()).isEqualTo(20);
            assertThat(payload.at("/assessment/jobDescriptionAnalysis/originalJobDescription").asText())
                    .isEqualTo("Senior backend role focused on Java and APIs");
            assertThat(payload.at("/candidate/experienceLevel").asText()).isEqualTo("SENIOR");
            assertThat(payload.at("/candidate/hardSkills/0").asText()).isEqualTo("Java");
            assertThat(payload.at("/candidate/softSkills/1").asText()).isEqualTo("Ownership");
            assertThat(payload.at("/supportedQuestions/0/questionType").asText()).isEqualTo("MULTIPLE_CHOICE");
            assertThat(payload.at("/supportedQuestions/0/selectedAlternativeId").asText())
                    .isEqualTo("22222222-2222-2222-2222-222222222222");
            assertThat(payload.at("/supportedQuestions/0/selectedAlternativeText").asText())
                    .isEqualTo("Clear root cause analysis");
            assertThat(payload.at("/supportedQuestions/0/alternatives/0/text").asText())
                    .isEqualTo("Clear root cause analysis");
            assertThat(payload.at("/supportedQuestions/0/projectUrl").isMissingNode()).isTrue();
            assertThat(payload.at("/supportedQuestions/1/questionType").asText()).isEqualTo("OPEN");
            assertThat(payload.at("/supportedQuestions/1/guideline").asText())
                    .isEqualTo("Discuss coupling, testability, and boundaries.");
            assertThat(payload.at("/supportedQuestions/1/textResponse").asText())
                    .isEqualTo("The current design favors testability over reuse.");
            assertThat(payload.at("/ignoredQuestionTypes/0").asText()).isEqualTo("PROJECT");
        });

        assertThat(analysis.getSummary()).isEqualTo("Candidate shows solid technical reasoning.");
        assertThat(analysis.getStrengths()).containsExactly(
                "Clear problem decomposition",
                "Good trade-off awareness"
        );
        assertThat(analysis.getAttentionPoints()).containsExactly("Should improve API validation");
        assertThat(analysis.getFinalExplanation())
                .isEqualTo("Overall, this attempt is a strong fit for recruiter review.");
    }

    @Test
    void shouldNormalizeNullListsInAttemptAnalysisOutput() throws Exception {
        AttemptPreAnalysisContext context = buildAttemptContext();
        String llmContent = new ObjectMapper().writeValueAsString(new LlmAttemptPreAnalysisOutput(
                "Candidate shows solid technical reasoning.",
                null,
                null,
                "Overall, this attempt is a strong fit for recruiter review."
        ));

        AttemptPreAnalysis.Analysis analysis = executeGateway(context, llmContent, payload -> {
            assertThat(payload.at("/attemptStatus").asText()).isEqualTo("COMPLETED");
        });

        assertThat(analysis.getStrengths()).isEmpty();
        assertThat(analysis.getAttentionPoints()).isEmpty();
        assertThat(analysis.getSummary()).isEqualTo("Candidate shows solid technical reasoning.");
        assertThat(analysis.getFinalExplanation())
                .isEqualTo("Overall, this attempt is a strong fit for recruiter review.");
    }

    @Test
    void shouldSanitizeFencedJsonResponseBeforeParsingAttemptAnalysis() throws Exception {
        AttemptPreAnalysisContext context = buildAttemptContext();
        String llmJson = new ObjectMapper().writeValueAsString(new LlmAttemptPreAnalysisOutput(
                "Candidate shows solid technical reasoning.",
                List.of("Clear problem decomposition"),
                List.of("Should improve API validation"),
                "Overall, this attempt is a strong fit for recruiter review."
        ));
        String llmContent = "Here is the analysis:\n```json\n" + llmJson + "\n```\nThanks.";

        AttemptPreAnalysis.Analysis analysis = executeGateway(context, llmContent, payload -> {
            assertThat(payload.at("/attemptId").asText()).isEqualTo("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        });

        assertThat(analysis.getSummary()).isEqualTo("Candidate shows solid technical reasoning.");
        assertThat(analysis.getStrengths()).containsExactly("Clear problem decomposition");
        assertThat(analysis.getAttentionPoints()).containsExactly("Should improve API validation");
        assertThat(analysis.getFinalExplanation())
                .isEqualTo("Overall, this attempt is a strong fit for recruiter review.");
    }

    @Test
    void shouldRejectAttemptAnalysisWhenRequiredFieldsAreBlank() throws Exception {
        AttemptPreAnalysisContext context = buildAttemptContext();
        String llmContent = new ObjectMapper().writeValueAsString(new LlmAttemptPreAnalysisOutput(
                " ",
                List.of("Strength"),
                List.of("Attention"),
                " "
        ));

        assertThatThrownBy(() -> executeGateway(context, llmContent, payload -> { }))
                .isInstanceOf(com.lia.liaprove.core.exceptions.question.QuestionPreAnalysisException.class)
                .hasMessageContaining("missing attempt summary");
    }

    @Test
    void shouldSanitizeFencedJsonResponseBeforeParsingQuestionPreAnalysis() throws Exception {
        PreAnalyzeQuestionUseCase.PreAnalysisCommand command = new PreAnalyzeQuestionUseCase.PreAnalysisCommand(
                "Controle de Versao",
                "Qual alternativa descreve controle de versao?",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.HARD,
                RelevanceLevel.FIVE,
                List.of("Sistema que registra alteracoes", "Compilador", "Garantia contra bugs")
        );
        String llmJson = new ObjectMapper().writeValueAsString(new LlmPreAnalysisOutput(
                List.of("Ajustar acentuacao do titulo."),
                List.of("Evitar alternativa absoluta."),
                List.of("Adicionar distrator sobre historico de releases."),
                "MEDIUM",
                List.of("Tema coerente com desenvolvimento de software.")
        ));
        String llmContent = "```json\n" + llmJson + "\n```";

        PreAnalyzeQuestionUseCase.PreAnalysisResult result = executeQuestionPreAnalysisGateway(command, llmContent);

        assertThat(result.languageSuggestions()).containsExactly("Ajustar acentuacao do titulo.");
        assertThat(result.biasOrAmbiguityWarnings()).containsExactly("Evitar alternativa absoluta.");
        assertThat(result.distractorSuggestions()).containsExactly("Adicionar distrator sobre historico de releases.");
        assertThat(result.difficultyLevelByLLM()).isEqualTo("MEDIUM");
        assertThat(result.topicConsistencyNotes()).containsExactly("Tema coerente com desenvolvimento de software.");
    }

    private AttemptPreAnalysis.Analysis executeGateway(
            AttemptPreAnalysisContext context,
            String llmContent,
            java.util.function.Consumer<JsonNode> requestAssertions) throws Exception {
        AtomicReference<String> capturedRequestBody = new AtomicReference<>();
        ObjectMapper objectMapper = new ObjectMapper();
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/chat/completions", exchange -> handleAttemptPreAnalysisRequest(exchange, capturedRequestBody, objectMapper, llmContent));
        server.start();

        try {
            HttpQuestionPreAnalysisGatewayImpl gateway = new HttpQuestionPreAnalysisGatewayImpl(
                    objectMapper,
                    "http://localhost:" + server.getAddress().getPort(),
                    "/chat/completions",
                    "test-api-key",
                    TEST_MODEL,
                    "",
                    1000,
                    1000,
                    "",
                    "",
                    QUESTION_PRE_ANALYSIS_SYSTEM_PROMPT,
                    "Submission preparation prompt",
                    ATTEMPT_PRE_ANALYSIS_SYSTEM_PROMPT,
                    "Job description analysis prompt"
            );

            AttemptPreAnalysis.Analysis analysis = gateway.generate(context);

            String requestBody = capturedRequestBody.get();
            assertThat(requestBody).isNotBlank();
            ProviderChatRequest request = objectMapper.readValue(requestBody, ProviderChatRequest.class);
            assertThat(request.model()).isEqualTo(TEST_MODEL);
            assertThat(request.messages()).hasSize(2);
            assertThat(request.messages().get(0).content()).isEqualTo(ATTEMPT_PRE_ANALYSIS_SYSTEM_PROMPT);
            assertThat(request.messages().get(1).content()).contains("attemptId");

            JsonNode payload = objectMapper.readTree(request.messages().get(1).content());
            requestAssertions.accept(payload);
            return analysis;
        } finally {
            server.stop(0);
        }
    }

    private PreAnalyzeQuestionUseCase.PreAnalysisResult executeQuestionPreAnalysisGateway(
            PreAnalyzeQuestionUseCase.PreAnalysisCommand command,
            String llmContent) throws Exception {
        AtomicReference<String> capturedRequestBody = new AtomicReference<>();
        ObjectMapper objectMapper = new ObjectMapper();
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/chat/completions", exchange -> handleAttemptPreAnalysisRequest(exchange, capturedRequestBody, objectMapper, llmContent));
        server.start();

        try {
            HttpQuestionPreAnalysisGatewayImpl gateway = new HttpQuestionPreAnalysisGatewayImpl(
                    objectMapper,
                    "http://localhost:" + server.getAddress().getPort(),
                    "/chat/completions",
                    "test-api-key",
                    TEST_MODEL,
                    "",
                    1000,
                    1000,
                    "",
                    "",
                    QUESTION_PRE_ANALYSIS_SYSTEM_PROMPT,
                    "Submission preparation prompt",
                    ATTEMPT_PRE_ANALYSIS_SYSTEM_PROMPT,
                    "Job description analysis prompt"
            );

            PreAnalyzeQuestionUseCase.PreAnalysisResult result = gateway.analyze(command);

            String requestBody = capturedRequestBody.get();
            assertThat(requestBody).isNotBlank();
            ProviderChatRequest request = objectMapper.readValue(requestBody, ProviderChatRequest.class);
            assertThat(request.model()).isEqualTo(TEST_MODEL);
            assertThat(request.messages()).hasSize(2);
            assertThat(request.messages().get(0).content()).isEqualTo(QUESTION_PRE_ANALYSIS_SYSTEM_PROMPT);
            assertThat(request.messages().get(1).content()).contains("Controle de Versao");

            return result;
        } finally {
            server.stop(0);
        }
    }

    private AttemptPreAnalysisContext buildAttemptContext() {
        UUID requesterId = UUID.randomUUID();
        UUID candidateId = UUID.randomUUID();

        Alternative correctAlternative = new Alternative(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "Clear root cause analysis",
                true
        );
        Alternative incorrectAlternative = new Alternative(
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                "Generic answer",
                false
        );

        MultipleChoiceQuestion multipleChoiceQuestion = new MultipleChoiceQuestion(List.of(correctAlternative, incorrectAlternative));
        multipleChoiceQuestion.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        multipleChoiceQuestion.setTitle("Debugging workflow");
        multipleChoiceQuestion.setDescription("Choose the best explanation for the failure.");

        OpenQuestion openQuestion = new OpenQuestion();
        openQuestion.setId(UUID.fromString("44444444-4444-4444-4444-444444444444"));
        openQuestion.setTitle("Architecture rationale");
        openQuestion.setDescription("Explain the trade-offs in the current design.");
        openQuestion.setGuideline("Discuss coupling, testability, and boundaries.");

        UserRecruiter recruiter = new UserRecruiter(
                requesterId,
                "Recruiter",
                "recruiter@example.com",
                "hashed-password",
                "Recruiter",
                "Bio",
                ExperienceLevel.SENIOR,
                UserRole.RECRUITER,
                5,
                0,
                List.of(),
                0.0f,
                LocalDateTime.now(),
                LocalDateTime.now(),
                UserStatus.ACTIVE
        );
        recruiter.setCompanyEmail("recruiter@example.com");
        recruiter.setCompanyName("Acme");

        UserProfessional candidate = new UserProfessional(
                candidateId,
                "Candidate",
                "candidate@example.com",
                "hashed-password",
                "Developer",
                "Bio",
                ExperienceLevel.SENIOR,
                UserRole.PROFESSIONAL,
                1,
                3,
                List.of(),
                82.0f,
                LocalDateTime.now(),
                LocalDateTime.now(),
                UserStatus.ACTIVE
        );
        candidate.setHardSkills(List.of("Java", "Spring Boot"));
        candidate.setSoftSkills(List.of("Communication", "Ownership"));

        AssessmentCriteriaWeights criteriaWeights = new AssessmentCriteriaWeights(50, 30, 20);
        JobDescriptionAnalysis jobDescriptionAnalysis = new JobDescriptionAnalysis(
                "Senior backend role focused on Java and APIs",
                java.util.Set.of(),
                List.of("Java", "Spring Boot"),
                List.of("Communication", "Ownership"),
                criteriaWeights
        );

        return new AttemptPreAnalysisContext(
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus.COMPLETED,
                83,
                new AttemptPreAnalysisContext.AssessmentContext(
                        "Personalized",
                        "Personalized assessment",
                        criteriaWeights,
                        jobDescriptionAnalysis
                ),
                new AttemptPreAnalysisContext.CandidateContext(
                        candidate.getExperienceLevel(),
                        candidate.getHardSkills(),
                        candidate.getSoftSkills()
                ),
                List.of(
                        new AttemptPreAnalysisContext.QuestionContext(
                                multipleChoiceQuestion.getId(),
                                QuestionType.MULTIPLE_CHOICE,
                                multipleChoiceQuestion.getTitle(),
                                multipleChoiceQuestion.getDescription(),
                                null,
                                null,
                                List.of(
                                        new AttemptPreAnalysisContext.AlternativeContext(correctAlternative),
                                        new AttemptPreAnalysisContext.AlternativeContext(incorrectAlternative)
                                ),
                                correctAlternative.id(),
                                correctAlternative.text(),
                                null
                        ),
                        new AttemptPreAnalysisContext.QuestionContext(
                                openQuestion.getId(),
                                QuestionType.OPEN,
                                openQuestion.getTitle(),
                                openQuestion.getDescription(),
                                openQuestion.getGuideline(),
                                openQuestion.getVisibility().name(),
                                List.of(),
                                null,
                                null,
                                "The current design favors testability over reuse."
                        )
                ),
                List.of(QuestionType.PROJECT)
        );
    }

    private AssessmentCriteriaWeights invokeResolveSuggestedWeights(LlmJobDescriptionAnalysisOutput output) throws Exception {
        Method method = HttpQuestionPreAnalysisGatewayImpl.class
                .getDeclaredMethod("resolveSuggestedWeights", LlmJobDescriptionAnalysisOutput.class);
        method.setAccessible(true);

        try {
            return (AssessmentCriteriaWeights) method.invoke(null, output);
        } catch (InvocationTargetException ex) {
            throw (Exception) ex.getTargetException();
        }
    }

    private void handleAttemptPreAnalysisRequest(
            HttpExchange exchange,
            AtomicReference<String> capturedRequestBody,
            ObjectMapper objectMapper,
            String llmContent) {
        try {
            capturedRequestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));

            ProviderChatResponse response = new ProviderChatResponse(List.of(
                    new ProviderChatResponse.Choice(
                            new ProviderChatResponse.Message(llmContent)
                    )
            ));

            byte[] responseBytes = objectMapper.writeValueAsBytes(response);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to handle mock provider request.", ex);
        } finally {
            exchange.close();
        }
    }
}
