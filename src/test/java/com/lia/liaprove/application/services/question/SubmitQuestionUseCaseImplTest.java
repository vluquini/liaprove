package com.lia.liaprove.application.services.question;

import com.lia.liaprove.application.gateways.question.QuestionGateway;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.OpenQuestion;
import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.usecases.question.QuestionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmitQuestionUseCaseImplTest {

    @Mock
    private QuestionGateway questionGateway;

    @Mock
    private QuestionFactory questionFactory;

    @InjectMocks
    private SubmitQuestionUseCaseImpl useCase;

    @Test
    void shouldRouteOpenQuestionSubmissionToOpenQuestionFactoryMethod() {
        QuestionCreateDto dto = new QuestionCreateDto(
                UUID.randomUUID(),
                "Explain generator trade-offs in Python",
                "Describe how generators affect memory usage and iteration semantics in Python applications.",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.MEDIUM,
                RelevanceLevel.HIGH,
                RelevanceLevel.HIGH,
                QuestionType.OPEN,
                List.of(),
                "Mention lazy evaluation and yield semantics.",
                OpenQuestionVisibility.PRIVATE
        );
        Question createdQuestion = new OpenQuestion("Mention lazy evaluation and yield semantics.", OpenQuestionVisibility.PRIVATE);

        when(questionGateway.existsByDescription(dto.description())).thenReturn(false);
        when(questionFactory.createOpenQuestion(dto)).thenReturn(createdQuestion);
        when(questionGateway.save(createdQuestion)).thenReturn(createdQuestion);

        Question savedQuestion = useCase.submit(dto);

        assertSame(createdQuestion, savedQuestion);
        verify(questionFactory).createOpenQuestion(dto);
        verify(questionFactory, never()).createProject(dto);
        verify(questionFactory, never()).createMultipleChoice(dto);
        verify(questionGateway).save(createdQuestion);
    }
}
