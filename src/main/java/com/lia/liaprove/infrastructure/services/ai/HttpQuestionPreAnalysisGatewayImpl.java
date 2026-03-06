package com.lia.liaprove.infrastructure.services.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.application.gateways.ai.QuestionPreAnalysisGateway;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.exceptions.question.QuestionPreAnalysisException;
import com.lia.liaprove.core.usecases.question.PreAnalyzeQuestionUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Collections;
import java.util.List;

/**
 * Adapter HTTP para pré-análise de questões com IA (sem Spring AI).
 */
@Service
public class HttpQuestionPreAnalysisGatewayImpl implements QuestionPreAnalysisGateway {

    private static final String SYSTEM_PROMPT = """
            You are an assistant specialized in reviewing technical assessment questions.
            Always return a valid JSON object with these fields:
            - relevanceByLLM: one of [ONE, TWO, THREE, FOUR, FIVE], based on how relevant the question is to the provided knowledgeAreas
            - languageSuggestions: string[] with suggestions about grammar, clarity, and coherence with the provided knowledgeAreas; include reformulations to reduce ambiguity
            - biasOrAmbiguityWarnings: string[]
            - distractorSuggestions: string[]
            - difficultyLevelByLLM: string (difficulty estimated by the LLM)
            - topicConsistencyNotes: string[]
            Keep responses concise and actionable. Always respond in Portuguese-BR.
            """;

    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final String apiKey;
    private final String model;
    private final String completionsPath;
    private final String referer;
    private final String appTitle;

    public HttpQuestionPreAnalysisGatewayImpl(
            ObjectMapper objectMapper,
            @Value("${ai.http.base-url:https://openrouter.ai/api/v1}") String baseUrl,
            @Value("${ai.http.chat-completions-path:/chat/completions}") String completionsPath,
            @Value("${ai.http.api-key:}") String apiKey,
            @Value("${ai.http.model:google/gemma-3-27b-it:free}") String model,
            @Value("${ai.http.connect-timeout-ms:5000}") int connectTimeoutMs,
            @Value("${ai.http.read-timeout-ms:20000}") int readTimeoutMs,
            @Value("${ai.http.referer:}") String referer,
            @Value("${ai.http.title:liaprove}") String appTitle) {

        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.completionsPath = completionsPath;
        this.referer = referer;
        this.appTitle = appTitle;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMs);
        requestFactory.setReadTimeout(readTimeoutMs);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public PreAnalyzeQuestionUseCase.PreAnalysisResult analyze(PreAnalyzeQuestionUseCase.PreAnalysisCommand command) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new QuestionPreAnalysisException("AI API key is not configured.");
        }

        try {
            String userPrompt = buildUserPrompt(command);
            ProviderChatRequest body = new ProviderChatRequest(
                    model,
                    0.2,
//                    new ProviderChatRequest.ResponseFormat("json_object"),
                    List.of(
                            new ProviderChatRequest.Message("system", SYSTEM_PROMPT),
                            new ProviderChatRequest.Message("user", userPrompt)
                    )
            );

            ProviderChatResponse response = restClient.post()
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

            String content = extractContent(response);
            LlmPreAnalysisOutput output = objectMapper.readValue(content, LlmPreAnalysisOutput.class);

            return new PreAnalyzeQuestionUseCase.PreAnalysisResult(
                    parseRelevance(output.relevanceByLLM()),
                    nullSafe(output.languageSuggestions()),
                    nullSafe(output.biasOrAmbiguityWarnings()),
                    nullSafe(output.distractorSuggestions()),
                    output.difficultyLevelByLLM(),
                    nullSafe(output.topicConsistencyNotes())
            );
        } catch (QuestionPreAnalysisException ex) {
            throw ex;
        } catch (RestClientResponseException ex) {
            throw new QuestionPreAnalysisException("AI provider error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString(), ex);
        } catch (JsonProcessingException ex) {
            throw new QuestionPreAnalysisException("Failed to parse AI provider response.", ex);
        }
    }

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

    private static <T> List<T> nullSafe(List<T> input) {
        return input == null ? Collections.emptyList() : input;
    }

    private record PromptInput(
            String title,
            String description,
            Object knowledgeAreas,
            Object difficultyByCommunity,
            Object relevanceByCommunity,
            List<String> alternatives
    ) {}

    private record LlmPreAnalysisOutput(
            String relevanceByLLM,
            List<String> languageSuggestions,
            List<String> biasOrAmbiguityWarnings,
            List<String> distractorSuggestions,
            String difficultyLevelByLLM,
            List<String> topicConsistencyNotes
    ) {}

    private record ProviderChatRequest(
            String model,
            double temperature,
//            ResponseFormat response_format,
            List<Message> messages
    ) {
        private record Message(String role, String content) {}
//        private record ResponseFormat(String type) {}
    }

    private record ProviderChatResponse(List<Choice> choices) {
        private record Choice(Message message) {}
        private record Message(String content) {}
    }
}



