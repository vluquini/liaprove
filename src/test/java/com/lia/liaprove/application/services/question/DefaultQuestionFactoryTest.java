package com.lia.liaprove.application.services.question;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.OpenQuestion;
import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DefaultQuestionFactoryTest {

    private final DefaultQuestionFactory factory = new DefaultQuestionFactory();

    @Test
    void shouldCreateOpenQuestionWithGuidelineAndVisibility() {
        QuestionCreateDto dto = new QuestionCreateDto(
                UUID.randomUUID(),
                "Explain generator trade-offs in Python",
                "Describe how generators affect memory usage and iteration semantics in Python applications.",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.MEDIUM,
                RelevanceLevel.FIVE,
                RelevanceLevel.FIVE,
                QuestionType.OPEN,
                List.of(),
                "Mention lazy evaluation and yield semantics.",
                OpenQuestionVisibility.SHARED
        );

        Question question = factory.createOpenQuestion(dto);

        OpenQuestion openQuestion = assertInstanceOf(OpenQuestion.class, question);
        assertEquals(QuestionType.OPEN, openQuestion.getQuestionType());
        assertEquals("Mention lazy evaluation and yield semantics.", openQuestion.getGuideline());
        assertEquals(OpenQuestionVisibility.SHARED, openQuestion.getVisibility());
        assertEquals(QuestionStatus.VOTING, openQuestion.getStatus());
        assertEquals(dto.title(), openQuestion.getTitle());
    }
}
