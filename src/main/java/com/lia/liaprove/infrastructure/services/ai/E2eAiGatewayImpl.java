package com.lia.liaprove.infrastructure.services.ai;

import com.lia.liaprove.application.gateways.ai.AttemptPreAnalysisContext;
import com.lia.liaprove.application.gateways.ai.AttemptPreAnalysisGateway;
import com.lia.liaprove.application.gateways.ai.JobDescriptionAnalysisGateway;
import com.lia.liaprove.application.gateways.ai.QuestionPreAnalysisGateway;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.AttemptPreAnalysis;
import com.lia.liaprove.core.domain.assessment.JobDescriptionAnalysis;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.usecases.question.PreAnalyzeQuestionUseCase;
import com.lia.liaprove.core.usecases.question.PrepareQuestionSubmissionUseCase;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Primary
@Profile("e2e")
public class E2eAiGatewayImpl
        implements QuestionPreAnalysisGateway, JobDescriptionAnalysisGateway, AttemptPreAnalysisGateway {

    @Override
    public PreAnalyzeQuestionUseCase.PreAnalysisResult analyze(PreAnalyzeQuestionUseCase.PreAnalysisCommand command) {
        return new PreAnalyzeQuestionUseCase.PreAnalysisResult(
                List.of("E2E: revise a clareza do enunciado."),
                List.of("E2E: verifique se nao ha ambiguidade no comando."),
                List.of("E2E: confirme se os distratores sao plausiveis."),
                "MEDIUM",
                List.of("E2E: tema coerente com desenvolvimento de software.")
        );
    }

    @Override
    public PrepareQuestionSubmissionUseCase.PreparedQuestion prepareForSubmission(
            PrepareQuestionSubmissionUseCase.PreparationCommand command) {
        return new PrepareQuestionSubmissionUseCase.PreparedQuestion(
                command.title(),
                command.description(),
                command.alternatives(),
                RelevanceLevel.FOUR
        );
    }

    @Override
    public JobDescriptionAnalysis analyze(String jobDescription) {
        return new JobDescriptionAnalysis(
                jobDescription,
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                List.of("Java", "Spring Boot", "SQL"),
                List.of("Comunicacao", "Colaboracao"),
                AssessmentCriteriaWeights.defaultWeights()
        );
    }

    @Override
    public AttemptPreAnalysis.Analysis generate(AttemptPreAnalysisContext context) {
        return new AttemptPreAnalysis.Analysis(
                "E2E: tentativa analisada com dados deterministos.",
                List.of("E2E: resposta cobre os pontos principais."),
                List.of("E2E: revisar detalhes antes da avaliacao final."),
                "E2E: analise gerada localmente para testes end-to-end."
        );
    }
}
