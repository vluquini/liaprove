package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.application.services.assessment.dto.SystemAssessmentType;
import com.lia.liaprove.core.domain.question.*;
import com.lia.liaprove.core.usecases.assessments.GenerateSystemAssessmentUseCase;

import java.util.*;

/**
 * Implementação do caso de uso responsável por gerar a lista de questões para avaliações do sistema.
 * Seleciona questões aleatoriamente seguindo proporções de dificuldade e áreas de conhecimento escolhidas.
 */
public class GenerateSystemAssessmentUseCaseImpl implements GenerateSystemAssessmentUseCase {

    private final QuestionGateway questionGateway;

    // Constantes para facilitar a alteração
    private static final int    TOTAL_QUESTIONS     = 10;
    // Easy Assessment
    private static final double EASY_RATIO_EASY     = 0.7;
    private static final double EASY_RATIO_MEDIUM   = 0.2;
    // Medium Assessment
    private static final double MEDIUM_RATIO_EASY   = 0.2;
    private static final double MEDIUM_RATIO_MEDIUM = 0.6;
    // Hard Assessment
    private static final double HARD_RATIO_EASY     = 0.1;
    private static final double HARD_RATIO_MEDIUM   = 0.3;

    public GenerateSystemAssessmentUseCaseImpl(QuestionGateway questionGateway) {
        this.questionGateway = questionGateway;
    }

    @Override
    public List<Question> createQuestions(KnowledgeArea knowledgeArea, DifficultyLevel difficultyLevel, SystemAssessmentType type) {
        if (type == SystemAssessmentType.PROJECT) {
            return questionGateway.findRandomByCriteria(Set.of(knowledgeArea), difficultyLevel, QuestionStatus.FINISHED, 1, ProjectQuestion.class);
        }

        // Caso MULTIPLE_CHOICE
        List<Question> finalQuestions = switch (difficultyLevel) {
            case EASY -> createEasyAssessmentQuestions(knowledgeArea);
            case MEDIUM -> createMediumAssessmentQuestions(knowledgeArea);
            default -> createHardAssessmentQuestions(knowledgeArea);
        };

        // Embaralha a ordem final das questões
        Collections.shuffle(finalQuestions);

        // Embaralha a ordem das alternativas dentro de cada questão de múltipla escolha
        shuffleAlternatives(finalQuestions);

        return finalQuestions;
    }

    private List<Question> createEasyAssessmentQuestions(KnowledgeArea knowledgeArea) {
        int easyCount = (int) (TOTAL_QUESTIONS * EASY_RATIO_EASY);
        int mediumCount = (int) (TOTAL_QUESTIONS * EASY_RATIO_MEDIUM);
        int hardCount = TOTAL_QUESTIONS - easyCount - mediumCount;
        return fetchAndCombineQuestions(knowledgeArea, easyCount, mediumCount, hardCount);
    }

    private List<Question> createMediumAssessmentQuestions(KnowledgeArea knowledgeArea) {
        int easyCount = (int) (TOTAL_QUESTIONS * MEDIUM_RATIO_EASY);
        int mediumCount = (int) (TOTAL_QUESTIONS * MEDIUM_RATIO_MEDIUM);
        int hardCount = TOTAL_QUESTIONS - easyCount - mediumCount;
        return fetchAndCombineQuestions(knowledgeArea, easyCount, mediumCount, hardCount);
    }

    private List<Question> createHardAssessmentQuestions(KnowledgeArea knowledgeArea) {
        int easyCount = (int) (TOTAL_QUESTIONS * HARD_RATIO_EASY);
        int mediumCount = (int) (TOTAL_QUESTIONS * HARD_RATIO_MEDIUM);
        int hardCount = TOTAL_QUESTIONS - easyCount - mediumCount;
        return fetchAndCombineQuestions(knowledgeArea, easyCount, mediumCount, hardCount);
    }

    private List<Question> fetchAndCombineQuestions(KnowledgeArea knowledgeArea, int easyCount, int mediumCount, int hardCount) {
        List<Question> easyQuestions   = questionGateway.findRandomByCriteria(Set.of(knowledgeArea), DifficultyLevel.EASY,
                                         QuestionStatus.FINISHED, easyCount, MultipleChoiceQuestion.class);
        List<Question> mediumQuestions = questionGateway.findRandomByCriteria(Set.of(knowledgeArea), DifficultyLevel.MEDIUM,
                                         QuestionStatus.FINISHED, mediumCount, MultipleChoiceQuestion.class);
        List<Question> hardQuestions   = questionGateway.findRandomByCriteria(Set.of(knowledgeArea), DifficultyLevel.HARD,
                                         QuestionStatus.FINISHED, hardCount, MultipleChoiceQuestion.class);

        List<Question> combined = new ArrayList<>();
        combined.addAll(easyQuestions);
        combined.addAll(mediumQuestions);
        combined.addAll(hardQuestions);
        return combined;
    }

    private void shuffleAlternatives(List<Question> questions) {
        questions.forEach(question -> {
            if (question instanceof MultipleChoiceQuestion mcq) {
                if (mcq.getAlternatives() != null && !mcq.getAlternatives().isEmpty()) {
                    List<Alternative> shuffledAlternatives = new ArrayList<>(mcq.getAlternatives());
                    Collections.shuffle(shuffledAlternatives);
                    mcq.setAlternatives(shuffledAlternatives);
                }
            }
        });
    }
}
