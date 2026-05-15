package com.lia.liaprove.infrastructure.services.ai;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.usecases.question.PreAnalyzeQuestionUseCase;
import com.lia.liaprove.core.usecases.question.PrepareQuestionSubmissionUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class E2eAiGatewayImplTest {

    private final E2eAiGatewayImpl gateway = new E2eAiGatewayImpl();

    @Test
    void shouldBePrimaryOnlyInE2eProfile() {
        assertThat(E2eAiGatewayImpl.class).hasAnnotation(Service.class);
        assertThat(E2eAiGatewayImpl.class).hasAnnotation(Primary.class);

        Profile profile = E2eAiGatewayImpl.class.getAnnotation(Profile.class);
        assertThat(profile.value()).containsExactly("e2e");
    }

    @Test
    void shouldReturnDeterministicQuestionPreAnalysis() {
        PreAnalyzeQuestionUseCase.PreAnalysisResult result = gateway.analyze(
                new PreAnalyzeQuestionUseCase.PreAnalysisCommand(
                        "Como validar transacoes em APIs REST?",
                        "Qual abordagem melhora a consistencia de dados em uma API REST com banco relacional?",
                        Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                        DifficultyLevel.MEDIUM,
                        RelevanceLevel.FOUR,
                        List.of("Usar transacoes no caso de uso.", "Ignorar rollback.", "Persistir parcialmente.")
                )
        );

        assertThat(result.languageSuggestions()).containsExactly("E2E: revise a clareza do enunciado.");
        assertThat(result.biasOrAmbiguityWarnings()).containsExactly("E2E: verifique se nao ha ambiguidade no comando.");
        assertThat(result.distractorSuggestions()).containsExactly("E2E: confirme se os distratores sao plausiveis.");
        assertThat(result.difficultyLevelByLLM()).isEqualTo("MEDIUM");
        assertThat(result.topicConsistencyNotes()).containsExactly("E2E: tema coerente com desenvolvimento de software.");
    }

    @Test
    void shouldPrepareQuestionSubmissionWithoutChangingDraft() {
        List<PrepareQuestionSubmissionUseCase.AlternativeInput> alternatives = List.of(
                new PrepareQuestionSubmissionUseCase.AlternativeInput("Usar transacoes no caso de uso.", true),
                new PrepareQuestionSubmissionUseCase.AlternativeInput("Ignorar rollback.", false),
                new PrepareQuestionSubmissionUseCase.AlternativeInput("Persistir parcialmente.", false)
        );
        PrepareQuestionSubmissionUseCase.PreparationCommand command =
                new PrepareQuestionSubmissionUseCase.PreparationCommand(
                        "Como validar transacoes em APIs REST?",
                        "Qual abordagem melhora a consistencia de dados em uma API REST com banco relacional?",
                        Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                        DifficultyLevel.MEDIUM,
                        RelevanceLevel.FOUR,
                        alternatives,
                        List.of("E2E: revise a clareza do enunciado."),
                        List.of(),
                        List.of(),
                        "MEDIUM",
                        List.of()
                );

        PrepareQuestionSubmissionUseCase.PreparedQuestion prepared = gateway.prepareForSubmission(command);

        assertThat(prepared.title()).isEqualTo(command.title());
        assertThat(prepared.description()).isEqualTo(command.description());
        assertThat(prepared.alternatives()).isEqualTo(alternatives);
        assertThat(prepared.relevanceByLLM()).isEqualTo(RelevanceLevel.FOUR);
    }
}
