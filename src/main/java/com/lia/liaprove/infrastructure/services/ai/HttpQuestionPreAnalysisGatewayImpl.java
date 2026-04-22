package com.lia.liaprove.infrastructure.services.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.application.gateways.ai.AttemptPreAnalysisContext;
import com.lia.liaprove.application.gateways.ai.AttemptPreAnalysisGateway;
import com.lia.liaprove.application.gateways.ai.QuestionPreAnalysisGateway;
import com.lia.liaprove.application.gateways.ai.JobDescriptionAnalysisGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.AttemptPreAnalysis;
import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.exceptions.question.QuestionPreAnalysisException;
import com.lia.liaprove.core.usecases.question.PreAnalyzeQuestionUseCase;
import com.lia.liaprove.core.usecases.question.PrepareQuestionSubmissionUseCase;
import com.lia.liaprove.infrastructure.dtos.ai.AcceptedSuggestionsInput;
import com.lia.liaprove.infrastructure.dtos.ai.JobDescriptionAnalysisInput;
import com.lia.liaprove.infrastructure.dtos.ai.LlmAlternative;
import com.lia.liaprove.infrastructure.dtos.ai.LlmAttemptPreAnalysisOutput;
import com.lia.liaprove.infrastructure.dtos.ai.LlmJobDescriptionAnalysisOutput;
import com.lia.liaprove.infrastructure.dtos.ai.LlmPreAnalysisOutput;
import com.lia.liaprove.infrastructure.dtos.ai.LlmSubmissionOutput;
import com.lia.liaprove.infrastructure.dtos.ai.PromptInput;
import com.lia.liaprove.infrastructure.dtos.ai.ProviderChatRequest;
import com.lia.liaprove.infrastructure.dtos.ai.ProviderChatResponse;
import com.lia.liaprove.infrastructure.dtos.ai.SubmissionPreparationInput;
import com.lia.liaprove.infrastructure.dtos.ai.SubmissionQuestionDraftInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

/**
 * HTTP adapter for LLM-based question pre-analysis and submission preparation.
 * It calls an external LLM provider via HTTP, parses the JSON response, and returns
 * structured suggestions or a prepared question, with fallback across configured models.
 */
@Service
public class HttpQuestionPreAnalysisGatewayImpl
        implements QuestionPreAnalysisGateway, JobDescriptionAnalysisGateway, AttemptPreAnalysisGateway {

    private static final Logger log = LoggerFactory.getLogger(HttpQuestionPreAnalysisGatewayImpl.class);

    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final String apiKey;
    private final List<String> models;
    private final String completionsPath;
    private final String referer;
    private final String appTitle;
    private final String preAnalysisSystemPrompt;
    private final String submissionPreparationSystemPrompt;
    private final String attemptPreAnalysisSystemPrompt;
    private final String jobDescriptionAnalysisSystemPrompt;

    public HttpQuestionPreAnalysisGatewayImpl(
            ObjectMapper objectMapper,
            @Value("${ai.http.base-url:}") String baseUrl,
            @Value("${ai.http.chat-completions-path:}") String completionsPath,
            @Value("${ai.http.api-key:}") String apiKey,
            @Value("${ai.http.principal.model:}") String model,
            @Value("${ai.http.fallbacks.models:}") String modelsCsv,
            @Value("${ai.http.connect-timeout-ms:5000}") int connectTimeoutMs,
            @Value("${ai.http.read-timeout-ms:20000}") int readTimeoutMs,
            @Value("${ai.http.referer:}") String referer,
            @Value("${ai.http.title:}") String appTitle,
            @Value("${ai.http.pre-analysis-system-prompt:}") String preAnalysisSystemPrompt,
            @Value("${ai.http.submission-preparation-system-prompt:}") String submissionPreparationSystemPrompt,
            @Value("${ai.http.attempt-pre-analysis-system-prompt:}") String attemptPreAnalysisSystemPrompt,
            @Value("${ai.http.job-description-analysis-system-prompt:}") String jobDescriptionAnalysisSystemPrompt) {

        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.models = resolveModels(model, modelsCsv);
        this.completionsPath = completionsPath;
        this.referer = referer;
        this.appTitle = appTitle;
        this.preAnalysisSystemPrompt = preAnalysisSystemPrompt;
        this.submissionPreparationSystemPrompt = submissionPreparationSystemPrompt;
        this.attemptPreAnalysisSystemPrompt = attemptPreAnalysisSystemPrompt;
        this.jobDescriptionAnalysisSystemPrompt = jobDescriptionAnalysisSystemPrompt;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMs);
        requestFactory.setReadTimeout(readTimeoutMs);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public AttemptPreAnalysis.Analysis generate(AttemptPreAnalysisContext context) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new QuestionPreAnalysisException("AI API key is not configured.");
        }

        try {
            String userPrompt = buildAttemptPreAnalysisUserPrompt(context);
            List<ProviderChatRequest.Message> messages = List.of(
                    new ProviderChatRequest.Message("system", attemptPreAnalysisSystemPrompt),
                    new ProviderChatRequest.Message("user", userPrompt)
            );

            return executeWithFallback(model -> {
                ProviderChatRequest body = new ProviderChatRequest(model, 0.2, messages);
                ProviderChatResponse response = callProvider(body);
                String content = sanitizeJsonContent(extractContent(response));
                LlmAttemptPreAnalysisOutput output = objectMapper.readValue(content, LlmAttemptPreAnalysisOutput.class);
                return toAttemptAnalysis(output);
            });
        } catch (JsonProcessingException ex) {
            throw new QuestionPreAnalysisException("Failed to parse AI provider response.", ex);
        }
    }

    /**
     * Sends the question draft to the LLM and returns structured suggestions.
     */
    @Override
    public PreAnalyzeQuestionUseCase.PreAnalysisResult analyze(PreAnalyzeQuestionUseCase.PreAnalysisCommand command) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new QuestionPreAnalysisException("AI API key is not configured.");
        }

        try {
            String userPrompt = buildUserPrompt(command);
            List<ProviderChatRequest.Message> messages = List.of(
                    new ProviderChatRequest.Message("system", preAnalysisSystemPrompt),
                    new ProviderChatRequest.Message("user", userPrompt)
            );

            return executeWithFallback(model -> {
                ProviderChatRequest body = new ProviderChatRequest(model, 0.2, messages);
                ProviderChatResponse response = callProvider(body);
                String content = sanitizeJsonContent(extractContent(response));
                LlmPreAnalysisOutput output = objectMapper.readValue(content, LlmPreAnalysisOutput.class);

                return new PreAnalyzeQuestionUseCase.PreAnalysisResult(
                        nullSafe(output.languageSuggestions()),
                        nullSafe(output.biasOrAmbiguityWarnings()),
                        nullSafe(output.distractorSuggestions()),
                        output.difficultyLevelByLLM(),
                        nullSafe(output.topicConsistencyNotes())
                );
            });
        } catch (JsonProcessingException ex) {
            throw new QuestionPreAnalysisException("Failed to parse AI provider response.", ex);
        }
    }

    /**
     * Sends the draft plus accepted suggestions to the LLM and returns a prepared question.
     */
    @Override
    public PrepareQuestionSubmissionUseCase.PreparedQuestion prepareForSubmission(
            PrepareQuestionSubmissionUseCase.PreparationCommand command) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new QuestionPreAnalysisException("AI API key is not configured.");
        }

        try {
            String userPrompt = buildSubmissionPreparationUserPrompt(command);
            List<ProviderChatRequest.Message> messages = List.of(
                    new ProviderChatRequest.Message("system", submissionPreparationSystemPrompt),
                    new ProviderChatRequest.Message("user", userPrompt)
            );

            return executeWithFallback(model -> {
                ProviderChatRequest body = new ProviderChatRequest(model, 0.2, messages);
                ProviderChatResponse response = callProvider(body);
                String content = extractContent(response);
                LlmSubmissionOutput output = objectMapper.readValue(content, LlmSubmissionOutput.class);

                List<PrepareQuestionSubmissionUseCase.AlternativeInput> alternatives = mapAlternatives(output.alternatives());
                if (alternatives.isEmpty()) {
                    alternatives = command.alternatives() == null ? List.of() : command.alternatives();
                }

                return new PrepareQuestionSubmissionUseCase.PreparedQuestion(
                        output.title(),
                        output.description(),
                        alternatives,
                        parseRelevance(output.relevanceByLLM())
                );
            });
        } catch (JsonProcessingException ex) {
            throw new QuestionPreAnalysisException("Failed to parse AI provider response.", ex);
        }
    }

    @Override
    public JobDescriptionAnalysis analyze(String jobDescription) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new QuestionPreAnalysisException("AI API key is not configured.");
        }

        try {
            String userPrompt = objectMapper.writeValueAsString(new JobDescriptionAnalysisInput(jobDescription));
            List<ProviderChatRequest.Message> messages = List.of(
                    new ProviderChatRequest.Message("system", jobDescriptionAnalysisSystemPrompt),
                    new ProviderChatRequest.Message("user", userPrompt)
            );

            return executeWithFallback(model -> {
                ProviderChatRequest body = new ProviderChatRequest(model, 0.2, messages);
                ProviderChatResponse response = callProvider(body);
                String content = extractContent(response);
                LlmJobDescriptionAnalysisOutput output = objectMapper.readValue(content, LlmJobDescriptionAnalysisOutput.class);

                return new JobDescriptionAnalysis(
                        output.originalJobDescription() == null || output.originalJobDescription().isBlank()
                                ? jobDescription
                                : output.originalJobDescription(),
                        parseKnowledgeAreas(output.suggestedKnowledgeAreas()),
                        nullSafe(output.suggestedHardSkills()),
                        nullSafe(output.suggestedSoftSkills()),
                        resolveSuggestedWeights(output)
                );
            });
        } catch (JsonProcessingException ex) {
            throw new QuestionPreAnalysisException("Failed to parse AI provider response.", ex);
        }
    }

    /**
     * Builds the JSON user prompt for the pre-analysis request.
     */
    private String buildUserPrompt(PreAnalyzeQuestionUseCase.PreAnalysisCommand command) throws JsonProcessingException {
        return objectMapper.writeValueAsString(new PromptInput(
                command.title(),
                command.description(),
                command.knowledgeAreas(),
                command.difficultyByCommunity(),
                command.relevanceByCommunity(),
                nullSafe(command.alternatives())
        ));
    }

    /**
     * Builds the JSON user prompt for submission preparation.
     */
    private String buildSubmissionPreparationUserPrompt(PrepareQuestionSubmissionUseCase.PreparationCommand command)
            throws JsonProcessingException {
        SubmissionPreparationInput input = new SubmissionPreparationInput(
                new SubmissionQuestionDraftInput(
                        command.title(),
                        command.description(),
                        command.knowledgeAreas(),
                        command.difficultyByCommunity(),
                        command.relevanceByCommunity(),
                        nullSafe(command.alternatives())
                ),
                new AcceptedSuggestionsInput(
                        nullSafe(command.acceptedLanguageSuggestions()),
                        nullSafe(command.acceptedBiasOrAmbiguityWarnings()),
                        nullSafe(command.acceptedDistractorSuggestions()),
                        command.acceptedDifficultyLevelByLLM(),
                        nullSafe(command.acceptedTopicConsistencyNotes())
                )
        );

        return objectMapper.writeValueAsString(input);
    }

    /**
     * Builds the JSON user prompt for attempt pre-analysis.
     */
    private String buildAttemptPreAnalysisUserPrompt(AttemptPreAnalysisContext context) throws JsonProcessingException {
        return objectMapper.writeValueAsString(context);
    }

    /**
     * Executes the HTTP call to the LLM provider.
     */
    private ProviderChatResponse callProvider(ProviderChatRequest body) {
        return restClient.post()
                .uri(completionsPath)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> {
                    headers.setBearerAuth(apiKey);
                    if (referer != null && !referer.isBlank()) {
                        headers.set("HTTP-Referer", referer);
                    }
                    if (appTitle != null && !appTitle.isBlank()) {
                        headers.set("X-Title", appTitle);
                    }
                })
                .body(body)
                .retrieve()
                .body(ProviderChatResponse.class);
    }

    /**
     * Extracts the content field from the provider response.
     */
    private static String extractContent(ProviderChatResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new QuestionPreAnalysisException("AI provider returned an empty response.");
        }
        ProviderChatResponse.Choice first = response.choices().getFirst();
        if (first.message() == null || first.message().content() == null || first.message().content().isBlank()) {
            throw new QuestionPreAnalysisException("AI provider response does not contain content.");
        }
        return first.message().content();
    }

    /**
     * Parses the relevance level string, returning a safe default when invalid.
     */
    private static RelevanceLevel parseRelevance(String value) {
        if (value == null || value.isBlank()) {
            return RelevanceLevel.THREE;
        }
        try {
            return RelevanceLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return RelevanceLevel.THREE;
        }
    }

    /**
     * Returns an empty list when the input is null.
     */
    private static <T> List<T> nullSafe(List<T> input) {
        return input == null ? Collections.emptyList() : input;
    }

    /**
     * Maps LLM alternatives to the use case alternative input.
     */
    private static List<PrepareQuestionSubmissionUseCase.AlternativeInput> mapAlternatives(List<LlmAlternative> alternatives) {
        if (alternatives == null) {
            return List.of();
        }
        return alternatives.stream()
                .filter(alt -> alt != null && alt.text() != null && !alt.text().isBlank())
                .map(alt -> new PrepareQuestionSubmissionUseCase.AlternativeInput(alt.text(), alt.correct()))
                .toList();
    }

    private static Set<KnowledgeArea> parseKnowledgeAreas(List<String> rawValues) {
        if (rawValues == null || rawValues.isEmpty()) {
            return Set.of();
        }

        EnumSet<KnowledgeArea> areas = EnumSet.noneOf(KnowledgeArea.class);
        for (String rawValue : rawValues) {
            if (rawValue == null || rawValue.isBlank()) {
                continue;
            }
            try {
                areas.add(KnowledgeArea.valueOf(rawValue.trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ignored) {
                // Ignore unknown values returned by the provider and keep only supported areas.
            }
        }
        return areas;
    }

    private static AssessmentCriteriaWeights resolveSuggestedWeights(LlmJobDescriptionAnalysisOutput output) {
        if (output.suggestedHardSkillsWeight() == null
                || output.suggestedSoftSkillsWeight() == null
                || output.suggestedExperienceWeight() == null) {
            return AssessmentCriteriaWeights.defaultWeights();
        }

        try {
            return new AssessmentCriteriaWeights(
                    output.suggestedHardSkillsWeight(),
                    output.suggestedSoftSkillsWeight(),
                    output.suggestedExperienceWeight()
            );
        } catch (IllegalArgumentException ex) {
            return AssessmentCriteriaWeights.defaultWeights();
        }
    }

    private static String sanitizeJsonContent(String content) {
        if (content == null) {
            throw new QuestionPreAnalysisException("AI provider response does not contain content.");
        }

        String sanitized = content.trim();
        sanitized = sanitized.replaceAll("(?s)^```(?:json)?\\s*", "");
        sanitized = sanitized.replaceAll("(?s)\\s*```\\s*$", "");

        int firstBrace = sanitized.indexOf('{');
        int lastBrace = sanitized.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            sanitized = sanitized.substring(firstBrace, lastBrace + 1).trim();
        }

        if (sanitized.isBlank()) {
            throw new QuestionPreAnalysisException("AI provider response does not contain parsable JSON.");
        }

        return sanitized;
    }

    private static AttemptPreAnalysis.Analysis toAttemptAnalysis(LlmAttemptPreAnalysisOutput output) {
        if (output == null) {
            throw new QuestionPreAnalysisException("AI provider response does not contain attempt analysis.");
        }
        if (output.summary() == null || output.summary().isBlank()) {
            throw new QuestionPreAnalysisException("AI provider response is missing attempt summary.");
        }
        if (output.finalExplanation() == null || output.finalExplanation().isBlank()) {
            throw new QuestionPreAnalysisException("AI provider response is missing attempt final explanation.");
        }

        try {
            return new AttemptPreAnalysis.Analysis(
                    output.summary(),
                    nullSafe(output.strengths()),
                    nullSafe(output.attentionPoints()),
                    output.finalExplanation()
            );
        } catch (IllegalArgumentException ex) {
            throw new QuestionPreAnalysisException("AI provider response is invalid.", ex);
        }
    }

    /**
     * Tries the LLM call across the configured models, returning the first successful result.
     */
    private <T> T executeWithFallback(ModelCall<T> call) {
        QuestionPreAnalysisException lastException = null;

        for (String candidate : models) {
            try {
                log.info("Trying AI provider model: {}", candidate);
                T result = call.execute(candidate);
                log.info("AI provider model succeeded: {}", candidate);
                return result;
            } catch (QuestionPreAnalysisException ex) {
                log.warn("AI provider model failed: {} - {}", candidate, ex.getMessage());
                lastException = ex;
            } catch (RestClientResponseException ex) {
                log.warn("AI provider model failed: {} - {} - {}", candidate, ex.getStatusCode(), ex.getResponseBodyAsString());
                lastException = new QuestionPreAnalysisException(
                        "AI provider error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString(), ex);
            } catch (JsonProcessingException ex) {
                log.warn("AI provider model failed: {} - failed to parse provider response: {}", candidate, ex.getOriginalMessage());
                lastException = new QuestionPreAnalysisException("Failed to parse AI provider response.", ex);
            }
        }

        if (lastException != null) {
            throw lastException;
        }
        throw new QuestionPreAnalysisException("AI provider failed across all fallback models.");
    }

    /**
     * Resolves the ordered list of models (principal first, then fallbacks).
     */
    private static List<String> resolveModels(String model, String modelsCsv) {
        List<String> resolved = new ArrayList<>();

        if (modelsCsv != null && !modelsCsv.isBlank()) {
            resolved.addAll(
                    Stream.of(modelsCsv.split(","))
                            .map(String::trim)
                            .filter(value -> !value.isBlank())
                            .toList()
            );
        }

        if (model != null && !model.isBlank()) {
            resolved.addFirst(model.trim());
        }

        List<String> unique = resolved.stream().distinct().toList();
        return unique;
    }

    /**
     * Functional interface for executing a model-specific call.
     */
    private interface ModelCall<T> {
        T execute(String model) throws JsonProcessingException;
    }
}
