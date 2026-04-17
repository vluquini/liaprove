package com.lia.liaprove.core.domain.question;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenQuestionTest {

    @Test
    void shouldExposeOpenQuestionTypeAndMetadata() {
        OpenQuestion question = new OpenQuestion("Use the rubric below.", OpenQuestionVisibility.SHARED);

        assertEquals(QuestionType.OPEN, question.getQuestionType());
        assertEquals("Use the rubric below.", question.getGuideline());
        assertEquals(OpenQuestionVisibility.SHARED, question.getVisibility());
    }

    @Test
    void shouldDefaultVisibilityToPrivateWhenNotProvided() {
        OpenQuestion question = new OpenQuestion("Use the rubric below.", null);

        assertEquals(OpenQuestionVisibility.PRIVATE, question.getVisibility());
    }
}
