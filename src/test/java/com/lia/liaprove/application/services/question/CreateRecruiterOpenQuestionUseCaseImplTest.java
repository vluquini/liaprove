package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.OpenQuestion;
import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.usecases.question.CreateRecruiterOpenQuestionUseCase;
import com.lia.liaprove.core.usecases.question.QuestionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRecruiterOpenQuestionUseCaseImplTest {

    @Mock
    private QuestionGateway questionGateway;

    @Mock
    private QuestionFactory questionFactory;

    @InjectMocks
    private CreateRecruiterOpenQuestionUseCaseImpl useCase;

    @Captor
    private ArgumentCaptor<QuestionCreateDto> questionCreateDtoCaptor;

    @Test
    void shouldCreateOpenQuestionWithNeutralLlmRelevanceAndPersistIt() {
        UUID authorId = UUID.randomUUID();
        CreateRecruiterOpenQuestionUseCase.OpenQuestionCommand command =
                new CreateRecruiterOpenQuestionUseCase.OpenQuestionCommand(
                        "Explain the open question lifecycle in the platform",
                        "Describe how open questions are created and stored in the system.",
                        Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                        DifficultyLevel.MEDIUM,
                        RelevanceLevel.FOUR,
                        "Mention the expected answer shape and scoring hints.",
                        OpenQuestionVisibility.SHARED
                );

        Question createdQuestion = new OpenQuestion(
                command.guideline(),
                command.visibility()
        );

        when(questionFactory.createOpenQuestion(any())).thenReturn(createdQuestion);
        when(questionGateway.save(createdQuestion)).thenReturn(createdQuestion);

        Question result = useCase.create(authorId, command);

        assertSame(createdQuestion, result);
        verify(questionFactory).createOpenQuestion(questionCreateDtoCaptor.capture());

        QuestionCreateDto capturedDto = questionCreateDtoCaptor.getValue();
        assertEquals(authorId, capturedDto.authorId());
        assertEquals(command.title(), capturedDto.title());
        assertEquals(command.description(), capturedDto.description());
        assertEquals(command.knowledgeAreas(), capturedDto.knowledgeAreas());
        assertEquals(command.difficultyByCommunity(), capturedDto.difficultyByCommunity());
        assertEquals(command.relevanceByCommunity(), capturedDto.relevanceByCommunity());
        assertEquals(RelevanceLevel.THREE, capturedDto.relevanceByLLM());
        assertEquals(QuestionType.OPEN, capturedDto.questionType());
        assertEquals(List.of(), capturedDto.alternatives());
        assertEquals(command.guideline(), capturedDto.guideline());
        assertEquals(command.visibility(), capturedDto.visibility());
        verify(questionGateway).save(createdQuestion);
    }

    @Test
    void shouldRejectNullCommand() {
        assertThatThrownBy(() -> useCase.create(UUID.randomUUID(), null))
                .isInstanceOf(NullPointerException.class);
    }
}
