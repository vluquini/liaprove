package com.lia.liaprove.infrastructure.configs;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionSeedScriptsTest {

    private static final String OPEN_QUESTION_TITLE = "Revision of Pull Request";

    @Test
    void shouldSeedOpenQuestionInH2Script() throws IOException {
        String script = readScript("src/main/resources/db/h2-populate-questions.sql");

        assertAll(
                () -> assertTrue(script.contains("'OPEN'")),
                () -> assertTrue(script.contains(OPEN_QUESTION_TITLE)),
                () -> assertTrue(script.contains("guideline")),
                () -> assertTrue(script.contains("visibility"))
        );
    }

    @Test
    void shouldSeedOpenQuestionInPostgresScript() throws IOException {
        String script = readScript("src/main/resources/db/populate-questions.sql");

        assertAll(
                () -> assertTrue(script.contains("'OPEN'")),
                () -> assertTrue(script.contains(OPEN_QUESTION_TITLE)),
                () -> assertTrue(script.contains("guideline")),
                () -> assertTrue(script.contains("visibility"))
        );
    }

    private String readScript(String relativePath) throws IOException {
        return Files.readString(Path.of(relativePath));
    }
}
