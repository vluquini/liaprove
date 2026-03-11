package com.lia.liaprove.infrastructure.services.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lia.liaprove.application.gateways.ai.QuestionPreAnalysisGateway;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.exceptions.question.QuestionPreAnalysisException;
import com.lia.liaprove.core.usecases.question.PreAnalyzeQuestionUseCase;
import com.lia.liaprove.core.usecases.question.PrepareQuestionSubmissionUseCase;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.ai.chat.client.ChatClient;

import java.util.ArrayList;
import java.util.List;

public class SpringAiQuestionPreAnalysisGatewayImpl implements QuestionPreAnalysisGateway {

    private static final String SYSTEM_PROMPT = """
            You are an assistant specialized in technical question quality review.
            Respond only with a valid JSON object. Do not include markdown.
            Analyze the question and return exactly these fields:
            - languageSuggestions: list of suggestions focused on grammar, clarity, coherence with knowledgeAreas, and reformulations to avoid ambiguity.
            - biasOrAmbiguityWarnings: list of potential bias or ambiguity warnings.
            - distractorSuggestions: list of improvements for alternatives, especially distractors. You may suggest adding new distractors.
            - difficultyLevelByLLM: brief text describing the estimated difficulty level (EASY, MEDIUM, HARD) and why.
            - topicConsistencyNotes: list of notes about topical consistency with selected knowledgeAreas.
            Allowed knowledge areas are strictly:
            SOFTWARE_DEVELOPMENT, DATABASE, CYBERSECURITY, NETWORKS, AI.
            Multiple choice questions must have between 3 and 5 alternatives total.
            Keep suggestions concise and practical. Always respond in Portuguese-BR.
            """;

    private static final String SUBMISSION_PREPARATION_SYSTEM_PROMPT = """
            You are an assistant that prepares a final technical question for submission.
            Respond only with a valid JSON object. Do not include markdown.
            Apply only suggestions explicitly accepted by the user.
            Keep all non-accepted aspects unchanged.
            Compute relevanceByLLM based on title, description and knowledgeAreas.
            Allowed knowledge areas are strictly:
            SOFTWARE_DEVELOPMENT, DATABASE, CYBERSECURITY, NETWORKS, AI.
            Return exactly:
            - title: final question title
            - description: final question description
            - alternatives: list of objects with fields { "text": "...", "correct": true|false }
            - relevanceByLLM: one of [ONE, TWO, THREE, FOUR, FIVE]
            For multiple choice:
            - Keep exactly one correct alternative (do not change which one is correct).
            - Ensure total alternatives count is between 3 and 5.
            - If acceptedDistractorSuggestions include adding a new alternative and total < 5, add it.
            - If total is already 5 and a new distractor is accepted, replace the weakest distractor.
            Always respond in Portuguese-BR.
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

    @Override
    public PrepareQuestionSubmissionUseCase.PreparedQuestion prepareForSubmission(PrepareQuestionSubmissionUseCase.PreparationCommand command) {
        if (!enabled) {
            throw new QuestionPreAnalysisException("AI pre-analysis integration is disabled.");
        }

        ChatClient.Builder builder = chatClientBuilderProvider.getIfAvailable();
        if (builder == null) {
            throw new QuestionPreAnalysisException("Spring AI ChatClient is not configured.");
        }

        String userPrompt = buildSubmissionPreparationUserPrompt(command);
        String rawResponse;

        try {
            rawResponse = builder.build()
                    .prompt()
                    .system(SUBMISSION_PREPARATION_SYSTEM_PROMPT)
                    .user(userPrompt)
                    .call()
                    .content();
        } catch (Exception ex) {
            throw new QuestionPreAnalysisException("Failed to call AI provider while preparing question submission.", ex);
        }

        if (rawResponse == null || rawResponse.isBlank()) {
            throw new QuestionPreAnalysisException("AI provider returned an empty response for question submission preparation.");
        }

        return parsePreparedQuestionResponse(rawResponse, command);
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

    private String buildSubmissionPreparationUserPrompt(PrepareQuestionSubmissionUseCase.PreparationCommand command) {
        String alternativesText = command.alternatives() == null || command.alternatives().isEmpty()
                ? "[]"
                : command.alternatives().toString();

        return """
                Question submission preparation request:
                {
                  "questionDraft": {
                    "title": "%s",
                    "description": "%s",
                    "knowledgeAreas": %s,
                    "difficultyByCommunity": "%s",
                    "relevanceByCommunity": "%s",
                    "alternatives": %s
                  },
                  "acceptedSuggestions": {
                    "languageSuggestions": %s,
                    "biasOrAmbiguityWarnings": %s,
                    "distractorSuggestions": %s,
                    "difficultyLevelByLLM": %s,
                    "topicConsistencyNotes": %s
                  }
                }
                """.formatted(
                sanitize(command.title()),
                sanitize(command.description()),
                command.knowledgeAreas(),
                command.difficultyByCommunity(),
                command.relevanceByCommunity(),
                alternativesText,
                toJsonArrayLiteral(command.acceptedLanguageSuggestions()),
                toJsonArrayLiteral(command.acceptedBiasOrAmbiguityWarnings()),
                toJsonArrayLiteral(command.acceptedDistractorSuggestions()),
                toJsonStringLiteral(command.acceptedDifficultyLevelByLLM()),
                toJsonArrayLiteral(command.acceptedTopicConsistencyNotes())
        );
    }

    private PreAnalyzeQuestionUseCase.PreAnalysisResult parseResponse(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);

            List<String> languageSuggestions = parseStringList(root.path("languageSuggestions"));
            List<String> biasOrAmbiguityWarnings = parseStringList(root.path("biasOrAmbiguityWarnings"));
            List<String> distractorSuggestions = parseStringList(root.path("distractorSuggestions"));
            String difficultyLevelByLLM = root.path("difficultyLevelByLLM").asText("");
            List<String> topicConsistencyNotes = parseStringList(root.path("topicConsistencyNotes"));

            return new PreAnalyzeQuestionUseCase.PreAnalysisResult(
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

    private PrepareQuestionSubmissionUseCase.PreparedQuestion parsePreparedQuestionResponse(
            String rawResponse,
            PrepareQuestionSubmissionUseCase.PreparationCommand fallbackCommand
    ) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            String title = root.path("title").asText(fallbackCommand.title());
            String description = root.path("description").asText(fallbackCommand.description());
            RelevanceLevel relevanceByLLM = parseRelevanceLevel(root.path("relevanceByLLM").asText("THREE"));
            List<PrepareQuestionSubmissionUseCase.AlternativeInput> alternatives = parseAlternativeInputs(root.path("alternatives"));

            if (alternatives.isEmpty()) {
                alternatives = fallbackCommand.alternatives() == null ? List.of() : fallbackCommand.alternatives();
            }

            return new PrepareQuestionSubmissionUseCase.PreparedQuestion(
                    title,
                    description,
                    alternatives,
                    relevanceByLLM
            );
        } catch (JsonProcessingException ex) {
            throw new QuestionPreAnalysisException("Failed to parse AI submission preparation response as JSON.", ex);
        } catch (IllegalArgumentException ex) {
            throw new QuestionPreAnalysisException("AI submission preparation response has invalid enum values.", ex);
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

    private static List<PrepareQuestionSubmissionUseCase.AlternativeInput> parseAlternativeInputs(JsonNode node) {
        List<PrepareQuestionSubmissionUseCase.AlternativeInput> values = new ArrayList<>();
        if (node != null && node.isArray()) {
            for (JsonNode item : node) {
                if (item == null || !item.isObject()) {
                    continue;
                }
                JsonNode textNode = item.path("text");
                if (!textNode.isTextual()) {
                    continue;
                }
                boolean correct = item.path("correct").asBoolean(false);
                values.add(new PrepareQuestionSubmissionUseCase.AlternativeInput(textNode.asText(), correct));
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

    private static String toJsonArrayLiteral(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "[]";
        }

        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(toJsonStringLiteral(values.get(i)));
        }
        builder.append("]");
        return builder.toString();
    }

    private static String toJsonStringLiteral(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + sanitize(value) + "\"";
    }
}
