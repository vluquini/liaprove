package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.usecases.question.CreateRecruiterOpenQuestionUseCase;
import com.lia.liaprove.core.usecases.question.QuestionFactory;

import java.util.List;
import java.util.Objects;

/**
 * Cria perguntas abertas para recrutadores usando o pipeline de persistência já existente.
 */
public class CreateRecruiterOpenQuestionUseCaseImpl implements CreateRecruiterOpenQuestionUseCase {

    private final QuestionGateway questionGateway;
    private final QuestionFactory questionFactory;

    public CreateRecruiterOpenQuestionUseCaseImpl(QuestionGateway questionGateway, QuestionFactory questionFactory) {
        this.questionGateway = Objects.requireNonNull(questionGateway, "questionGateway must not be null");
        this.questionFactory = Objects.requireNonNull(questionFactory, "questionFactory must not be null");
    }

    @Override
    public Question create(java.util.UUID authorId, OpenQuestionCommand command) {
        Objects.requireNonNull(authorId, "authorId must not be null");
        Objects.requireNonNull(command, "command must not be null");

        QuestionCreateDto dto = new QuestionCreateDto(
                authorId,
                command.title(),
                command.description(),
                command.knowledgeAreas(),
                command.difficultyByCommunity(),
                command.relevanceByCommunity(),
                // Open questions do not go through pre-analysis in this flow, so keep a neutral baseline.
                RelevanceLevel.THREE,
                QuestionType.OPEN,
                List.of(),
                command.guideline(),
                command.visibility()
        );

        Question question = questionFactory.createOpenQuestion(dto);
        return questionGateway.save(question);
    }
}
