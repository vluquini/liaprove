package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.usecases.question.UpdateQuestionUseCase;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementação do caso de uso para atualização de uma questão existente.
 */
public class UpdateQuestionUseCaseImpl implements UpdateQuestionUseCase {

    private final QuestionGateway questionGateway;

    public UpdateQuestionUseCaseImpl(QuestionGateway questionGateway) {
        this.questionGateway = questionGateway;
    }

    @Override
    public Optional<Question> execute(UUID questionId, UpdateQuestionCommand command) {
        return questionGateway.findById(questionId).map(question -> {
            command.title().ifPresent(question::setTitle);
            command.description().ifPresent(question::setDescription);
            command.knowledgeAreas().ifPresent(question::setKnowledgeAreas);
            command.difficultyLevel().ifPresent(question::setDifficultyByCommunity);
            command.relevanceByCommunity().ifPresent(question::setRelevanceByCommunity);
            command.submissionDate().ifPresent(question::setSubmissionDate);
            command.relevanceByLLM().ifPresent(question::setRelevanceByLLM);
            command.recruiterUsageCount().ifPresent(question::setRecruiterUsageCount);

            if (question instanceof MultipleChoiceQuestion mcQuestion) {
                command.alternatives().ifPresent(mcQuestion::setAlternatives);
            } else if (question instanceof ProjectQuestion pQuestion) {
                command.projectUrl().ifPresent(pQuestion::setProjectUrl);
            }

            return questionGateway.update(question);
        });
    }

}
