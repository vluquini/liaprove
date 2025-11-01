package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.usecases.question.SubmitQuestionUseCase;

import java.util.UUID;

/**
 * Implementação do caso de uso para submissão de uma nova questão.
 */
public class SubmitQuestionUseCaseImpl implements SubmitQuestionUseCase {

    private final QuestionGateway questionGateway;

    public SubmitQuestionUseCaseImpl(QuestionGateway questionGateway) {
        this.questionGateway = questionGateway;
    }

    @Override
    public Question execute(SubmitQuestionCommand command) {
        Question newQuestion;
        UUID questionId = UUID.randomUUID(); // Gera um novo ID para a Question

        switch (command.questionType()) {
            case MULTIPLE_CHOICE:
                if (command.alternatives() == null || command.alternatives().isEmpty()) {
                    throw new IllegalArgumentException("Multiple choice question must have alternatives.");
                }
                newQuestion = new MultipleChoiceQuestion(
                        questionId,
                        command.authorId(),
                        command.title(),
                        command.description(),
                        command.knowledgeAreas(),
                        command.difficultyLevel(),
                        command.relevanceByCommunity(),
                        command.submissionDate(),
                        command.status(),
                        command.relevanceByLLM(),
                        command.recruiterUsageCount(),
                        command.alternatives()
                );
                break;
            case PROJECT:
                if (command.projectUrl() == null || command.projectUrl().isBlank()) {
                    throw new IllegalArgumentException("Project question must have a project URL.");
                }
                newQuestion = new ProjectQuestion(
                        questionId,
                        command.authorId(),
                        command.title(),
                        command.description(),
                        command.knowledgeAreas(),
                        command.difficultyLevel(),
                        command.relevanceByCommunity(),
                        command.submissionDate(),
                        command.status(),
                        command.relevanceByLLM(),
                        command.recruiterUsageCount(),
                        command.projectUrl()
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown question type: " + command.questionType());
        }

        return questionGateway.save(newQuestion);
    }

}
