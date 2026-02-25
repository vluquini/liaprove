package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.*;
import com.lia.liaprove.core.usecases.assessments.SystemAssessmentFactory;

import java.util.*;

public class SystemAssessmentFactoryImpl implements SystemAssessmentFactory {

    private final QuestionGateway questionGateway;

    // Constantes para facilitar a alteração
    private static final int    TOTAL_QUESTIONS = 10;
    // Easy Assessment
    private static final double EASY_RATIO_EASY   = 0.7;
    private static final double EASY_RATIO_MEDIUM = 0.2;
    // Medium Assessment
    private static final double MEDIUM_RATIO_EASY   = 0.2;
    private static final double MEDIUM_RATIO_MEDIUM = 0.6;
    // Hard Assessment
    private static final double HARD_RATIO_EASY   = 0.1;
    private static final double HARD_RATIO_MEDIUM = 0.3;

    public SystemAssessmentFactoryImpl(QuestionGateway questionGateway) {
        this.questionGateway = questionGateway;
    }

    @Override
    public List<Question> createQuestions(Set<KnowledgeArea> knowledgeAreas, DifficultyLevel difficultyLevel) {
        List<Question> finalQuestions = switch (difficultyLevel) {
            case EASY -> createEasyAssessmentQuestions(knowledgeAreas);
            case MEDIUM -> createMediumAssessmentQuestions(knowledgeAreas);
            default -> createHardAssessmentQuestions(knowledgeAreas);
        };

        // Embaralha a ordem final das questões
        Collections.shuffle(finalQuestions);

        // Embaralha a ordem das alternativas dentro de cada questão de múltipla escolha
        shuffleAlternatives(finalQuestions);

        return finalQuestions;
    }

    private List<Question> createEasyAssessmentQuestions(Set<KnowledgeArea> knowledgeAreas) {
        int easyCount = (int) (TOTAL_QUESTIONS * EASY_RATIO_EASY);
        int mediumCount = (int) (TOTAL_QUESTIONS * EASY_RATIO_MEDIUM);
        int hardCount = TOTAL_QUESTIONS - easyCount - mediumCount;
        return fetchAndCombineQuestions(knowledgeAreas, easyCount, mediumCount, hardCount);
    }

    private List<Question> createMediumAssessmentQuestions(Set<KnowledgeArea> knowledgeAreas) {
        int easyCount = (int) (TOTAL_QUESTIONS * MEDIUM_RATIO_EASY);
        int mediumCount = (int) (TOTAL_QUESTIONS * MEDIUM_RATIO_MEDIUM);
        int hardCount = TOTAL_QUESTIONS - easyCount - mediumCount;
        return fetchAndCombineQuestions(knowledgeAreas, easyCount, mediumCount, hardCount);
    }

    private List<Question> createHardAssessmentQuestions(Set<KnowledgeArea> knowledgeAreas) {
        int easyCount = (int) (TOTAL_QUESTIONS * HARD_RATIO_EASY);
        int mediumCount = (int) (TOTAL_QUESTIONS * HARD_RATIO_MEDIUM);
        int hardCount = TOTAL_QUESTIONS - easyCount - mediumCount;
        return fetchAndCombineQuestions(knowledgeAreas, easyCount, mediumCount, hardCount);
    }

    private List<Question> fetchAndCombineQuestions(Set<KnowledgeArea> knowledgeAreas, int easyCount, int mediumCount, int hardCount) {
        List<Question> easyQuestions   = questionGateway.findRandomByCriteria(knowledgeAreas, DifficultyLevel.EASY, easyCount);
        List<Question> mediumQuestions = questionGateway.findRandomByCriteria(knowledgeAreas, DifficultyLevel.MEDIUM, mediumCount);
        List<Question> hardQuestions   = questionGateway.findRandomByCriteria(knowledgeAreas, DifficultyLevel.HARD, hardCount);

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
