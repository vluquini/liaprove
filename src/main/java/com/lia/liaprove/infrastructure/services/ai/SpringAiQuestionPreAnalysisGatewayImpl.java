package com.lia.liaprove.infrastructure.services.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.application.gateways.ai.QuestionPreAnalysisGateway;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.exceptions.question.QuestionPreAnalysisException;
import com.lia.liaprove.core.usecases.question.PreAnalyzeQuestionUseCase;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.ai.chat.client.ChatClient;

import java.util.ArrayList;
import java.util.List;

public class SpringAiQuestionPreAnalysisGatewayImpl implements QuestionPreAnalysisGateway {

    private static final String SYSTEM_PROMPT = """
            You are an assistant specialized in technical question quality review.
            Respond only with a valid JSON object. Do not include markdown.
            Analyze the question and return exactly these fields:
            - relevanceByLLM: one of [ONE, TWO, THREE, FOUR, FIVE]
              Choose it based on alignment between question content and the provided knowledgeAreas.
            - languageSuggestions: list of suggestions focused on grammar, clarity, coherence with knowledgeAreas, and reformulations to avoid ambiguity.
            - biasOrAmbiguityWarnings: list of potential bias or ambiguity warnings.
            - distractorSuggestions: list of improvements for alternatives, especially distractors.
            - difficultyLevelByLLM: brief text describing the estimated difficulty level (EASY, INTERMEDIATE, ADVANCED) and why.
            - topicConsistencyNotes: list of notes about topical consistency with selected knowledgeAreas.
            Keep suggestions concise and practical. Always respond in Portuguese-BR.
            """;

    private final ObjectProvider<ChatClient.Builder> chatClientBuilderProvider;
    private final ObjectMapper objectMapper;
    private final boolean enabled;

    public SpringAiQuestionPreAnalysisGatewayImpl(ObjectProvider<ChatClient.Builder> chatClientBuilderProvider,
                                                  ObjectMapper objectMapper, boolean enabled) {
        this.chatClientBuilderProvider = chatClientBuilderProvider;
        this.objectMapper = objectMapper;
        this.enabled = enabled;
    }

    @Override
    public PreAnalyzeQuestionUseCase.PreAnalysisResult analyze(PreAnalyzeQuestionUseCase.PreAnalysisCommand command) {
        if (!enabled) {
            throw new QuestionPreAnalysisException("AI pre-analysis integration is disabled.");
        }

        ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();
        if (builder == null) {
            throw new QuestionPreAnalysisException("Spring AI ChatClient is not configured.");
        }

        String userPrompt = buildUserPrompt(command);
        String rawResponse;

        try {
            rawResponse = builder.build()
                    .prompt()
                    .system(SYSTEM_PROMPT)
                    .user(userPrompt)
                    .call()
                    .content();
        } catch (Exception ex) {
            throw new QuestionPreAnalysisException("Failed to call AI provider for question pre-analysis.", ex);
        }

        if (rawResponse == null || rawResponse.isBlank()) {
            throw new QuestionPreAnalysisException("AI provider returned an empty pre-analysis response.");
        }

        return parseResponse(rawResponse);
    }

    private String buildUserPrompt(PreAnalyzeQuestionUseCase.PreAnalysisCommand command) {
        String alternativesText = command.alternatives() == null || command.alternatives().isEmpty()
                ? "[]"
                : command.alternatives().toString();

        return """
                Question pre-analysis request:
                {
                  "title": "%s",
                  "description": "%s",
                  "knowledgeAreas": %s,
                  "difficultyByCommunity": "%s",
                  "relevanceByCommunity": "%s",
                  "alternatives": %s
                }
                """.formatted(
                sanitize(command.title()),
                sanitize(command.description()),
                command.knowledgeAreas(),
                command.difficultyByCommunity(),
                command.relevanceByCommunity(),
                alternativesText
        );
    }

    private PreAnalyzeQuestionUseCase.PreAnalysisResult parseResponse(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);

            RelevanceLevel relevanceByLLM = parseRelevanceLevel(root.path("relevanceByLLM").asText("THREE"));
            List<String> languageSuggestions = parseStringList(root.path("languageSuggestions"));
            List<String> biasOrAmbiguityWarnings = parseStringList(root.path("biasOrAmbiguityWarnings"));
            List<String> distractorSuggestions = parseStringList(root.path("distractorSuggestions"));
            String difficultyLevelByLLM = root.path("difficultyLevelByLLM").asText("");
            List<String> topicConsistencyNotes = parseStringList(root.path("topicConsistencyNotes"));

            return new PreAnalyzeQuestionUseCase.PreAnalysisResult(
                    relevanceByLLM,
                    languageSuggestions,
                    biasOrAmbiguityWarnings,
                    distractorSuggestions,
                    difficultyLevelByLLM,
                    topicConsistencyNotes
            );
        } catch (JsonProcessingException ex) {
            throw new QuestionPreAnalysisException("Failed to parse AI pre-analysis response as JSON.", ex);
        } catch (IllegalArgumentException ex) {
            throw new QuestionPreAnalysisException("AI pre-analysis response has invalid enum values.", ex);
        }
    }

    private static RelevanceLevel parseRelevanceLevel(String value) {
        return RelevanceLevel.valueOf(value.trim().toUpperCase());
    }

    private static List<String> parseStringList(JsonNode node) {
        List<String> values = new ArrayList<>();
        if (node != null && node.isArray()) {
            for (JsonNode item : node) {
                if (item != null && item.isTextual()) {
                    values.add(item.asText());
                }
            }
        }
        return values;
    }

    private static String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\\\"");
    }
}
