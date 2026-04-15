package com.lia.liaprove.application.services.question;

import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.exceptions.user.InvalidUserDataException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuestionValidatorTest {

    @Test
    void shouldValidateOpenQuestionMetadataWhenVisibilityIsProvided() {
        QuestionCreateDto dto = new QuestionCreateDto(
                UUID.randomUUID(),
                "How would you explain the result?",
                "Describe your reasoning using the rubric provided in the prompt.",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.MEDIUM,
                RelevanceLevel.FIVE,
                null,
                QuestionType.OPEN,
                List.of(),
                "Focus on clarity and correctness.",
                OpenQuestionVisibility.PRIVATE
        );

        assertDoesNotThrow(() -> QuestionValidator.validate(dto));
    }

    @Test
    void shouldRejectOpenQuestionWithAlternatives() {
        QuestionCreateDto dto = new QuestionCreateDto(
                UUID.randomUUID(),
                "How would you explain the result?",
                "Describe your reasoning using the rubric provided in the prompt.",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.MEDIUM,
                RelevanceLevel.FIVE,
                null,
                QuestionType.OPEN,
                List.of(new Alternative(null, "Because it is correct.", true)),
                "Focus on clarity and correctness.",
                OpenQuestionVisibility.SHARED
        );

        assertThrows(InvalidUserDataException.class, () -> QuestionValidator.validate(dto));
    }

    @Test
    void shouldRejectOpenQuestionWithoutVisibilityEvenWhenGuidelineIsOmitted() {
        QuestionCreateDto dto = new QuestionCreateDto(
                UUID.randomUUID(),
                "How would you explain the result?",
                "Describe your reasoning using the rubric provided in the prompt.",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.MEDIUM,
                RelevanceLevel.FIVE,
                null,
                QuestionType.OPEN,
                List.of(),
                null,
                null
        );

        assertThrows(InvalidUserDataException.class, () -> QuestionValidator.validate(dto));
    }

    @Test
    void shouldDefaultLegacyConstructorToMultipleChoiceWhenAlternativesExist() {
        QuestionCreateDto dto = new QuestionCreateDto(
                UUID.randomUUID(),
                "How would you explain the result?",
                "Describe your reasoning using the rubric provided in the prompt.",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.MEDIUM,
                RelevanceLevel.FIVE,
                null,
                List.of(new Alternative(null, "Because it is correct.", true))
        );

        assertDoesNotThrow(() -> QuestionValidator.validate(dto));
        org.junit.jupiter.api.Assertions.assertEquals(QuestionType.MULTIPLE_CHOICE, dto.questionType());
    }

    @Test
    void shouldDefaultLegacyConstructorToProjectWhenAlternativesAreAbsent() {
        QuestionCreateDto dto = new QuestionCreateDto(
                UUID.randomUUID(),
                "How would you explain the result?",
                "Describe your reasoning using the rubric provided in the prompt.",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.MEDIUM,
                RelevanceLevel.FIVE,
                null,
                List.of()
        );

        assertDoesNotThrow(() -> QuestionValidator.validate(dto));
        org.junit.jupiter.api.Assertions.assertEquals(QuestionType.PROJECT, dto.questionType());
    }
}
