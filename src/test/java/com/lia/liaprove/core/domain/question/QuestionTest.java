package com.lia.liaprove.core.domain.question;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class QuestionTest {

    @Test
    void shouldInitializeCommonFieldsThroughProjectQuestionConstructor() {
        UUID id = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        Set<KnowledgeArea> knowledgeAreas = Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT);
        LocalDateTime submissionDate = LocalDateTime.of(2026, 6, 18, 10, 0);
        LocalDateTime votingEndDate = LocalDateTime.of(2026, 6, 25, 10, 0);

        ProjectQuestion question = new ProjectQuestion(
                id,
                authorId,
                "Build an API",
                "Build a REST API with persistence and tests.",
                knowledgeAreas,
                DifficultyLevel.MEDIUM,
                RelevanceLevel.FOUR,
                submissionDate,
                votingEndDate,
                QuestionStatus.VOTING,
                RelevanceLevel.THREE,
                2
        );

        assertThat(question.getId()).isEqualTo(id);
        assertThat(question.getAuthorId()).isEqualTo(authorId);
        assertThat(question.getTitle()).isEqualTo("Build an API");
        assertThat(question.getDescription()).isEqualTo("Build a REST API with persistence and tests.");
        assertThat(question.getKnowledgeAreas()).containsExactly(KnowledgeArea.SOFTWARE_DEVELOPMENT);
        assertThat(question.getDifficultyByCommunity()).isEqualTo(DifficultyLevel.MEDIUM);
        assertThat(question.getRelevanceByCommunity()).isEqualTo(RelevanceLevel.FOUR);
        assertThat(question.getSubmissionDate()).isEqualTo(submissionDate);
        assertThat(question.getVotingEndDate()).isEqualTo(votingEndDate);
        assertThat(question.getStatus()).isEqualTo(QuestionStatus.VOTING);
        assertThat(question.getRelevanceByLLM()).isEqualTo(RelevanceLevel.THREE);
        assertThat(question.getRecruiterUsageCount()).isEqualTo(2);
    }

    @Test
    void shouldUpdateCommonFieldsWithSetters() {
        ProjectQuestion question = new ProjectQuestion();
        UUID id = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        LocalDateTime submissionDate = LocalDateTime.of(2026, 6, 18, 11, 0);
        LocalDateTime votingEndDate = LocalDateTime.of(2026, 6, 25, 11, 0);

        question.setId(id);
        question.setAuthorId(authorId);
        question.setTitle("Review SQL indexing");
        question.setDescription("Explain the effects of indexes on query performance.");
        question.setKnowledgeAreas(Set.of(KnowledgeArea.DATABASE, KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.HARD);
        question.setRelevanceByCommunity(RelevanceLevel.FIVE);
        question.setSubmissionDate(submissionDate);
        question.setVotingEndDate(votingEndDate);
        question.setStatus(QuestionStatus.APPROVED);
        question.setRelevanceByLLM(RelevanceLevel.FOUR);
        question.setRecruiterUsageCount(1);

        assertThat(question.getId()).isEqualTo(id);
        assertThat(question.getAuthorId()).isEqualTo(authorId);
        assertThat(question.getTitle()).isEqualTo("Review SQL indexing");
        assertThat(question.getDescription()).isEqualTo("Explain the effects of indexes on query performance.");
        assertThat(question.getKnowledgeAreas())
                .containsExactlyInAnyOrder(KnowledgeArea.DATABASE, KnowledgeArea.SOFTWARE_DEVELOPMENT);
        assertThat(question.getDifficultyByCommunity()).isEqualTo(DifficultyLevel.HARD);
        assertThat(question.getRelevanceByCommunity()).isEqualTo(RelevanceLevel.FIVE);
        assertThat(question.getSubmissionDate()).isEqualTo(submissionDate);
        assertThat(question.getVotingEndDate()).isEqualTo(votingEndDate);
        assertThat(question.getStatus()).isEqualTo(QuestionStatus.APPROVED);
        assertThat(question.getRelevanceByLLM()).isEqualTo(RelevanceLevel.FOUR);
        assertThat(question.getRecruiterUsageCount()).isEqualTo(1);
    }

    @Test
    void shouldChangeStatusWhenNewStatusIsValid() {
        ProjectQuestion question = new ProjectQuestion();
        question.setStatus(QuestionStatus.VOTING);

        question.setStatus(QuestionStatus.REJECTED);

        assertThat(question.getStatus()).isEqualTo(QuestionStatus.REJECTED);
    }

    @Test
    void shouldRejectNullStatus() {
        ProjectQuestion question = new ProjectQuestion();

        assertThatThrownBy(() -> question.setStatus(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The new status cannot be null.");
    }

    @Test
    void shouldKeepStatusWhenSettingSameStatus() {
        ProjectQuestion question = new ProjectQuestion();
        question.setStatus(QuestionStatus.APPROVED);

        assertDoesNotThrow(() -> question.setStatus(QuestionStatus.APPROVED));

        assertThat(question.getStatus()).isEqualTo(QuestionStatus.APPROVED);
    }

    @Test
    void shouldStoreKnowledgeAreasDefensivelyFromConstructorAndSetter() {
        Set<KnowledgeArea> constructorAreas = new HashSet<>();
        constructorAreas.add(KnowledgeArea.DATABASE);
        ProjectQuestion question = new ProjectQuestion(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Build an API",
                "Build a REST API with persistence and tests.",
                constructorAreas,
                DifficultyLevel.MEDIUM,
                RelevanceLevel.FOUR,
                LocalDateTime.of(2026, 6, 18, 10, 0),
                LocalDateTime.of(2026, 6, 25, 10, 0),
                QuestionStatus.VOTING,
                RelevanceLevel.THREE,
                0
        );

        constructorAreas.add(KnowledgeArea.AI);
        assertThat(question.getKnowledgeAreas()).containsExactly(KnowledgeArea.DATABASE);
        assertThatThrownBy(() -> question.getKnowledgeAreas().add(KnowledgeArea.AI))
                .isInstanceOf(UnsupportedOperationException.class);

        Set<KnowledgeArea> setterAreas = new HashSet<>();
        setterAreas.add(KnowledgeArea.NETWORKS);
        question.setKnowledgeAreas(setterAreas);
        setterAreas.add(KnowledgeArea.CYBERSECURITY);

        assertThat(question.getKnowledgeAreas()).containsExactly(KnowledgeArea.NETWORKS);
        assertThatThrownBy(() -> question.getKnowledgeAreas().add(KnowledgeArea.CYBERSECURITY))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldAllowReplacingKnowledgeAreasWithAnotherValidSet() {
        ProjectQuestion question = new ProjectQuestion();
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT, KnowledgeArea.DATABASE));

        question.setKnowledgeAreas(Set.of(KnowledgeArea.AI));

        assertThat(question.getKnowledgeAreas()).containsExactly(KnowledgeArea.AI);
    }

    @Test
    void shouldRejectInvalidKnowledgeAreas() {
        ProjectQuestion question = new ProjectQuestion();

        assertThatThrownBy(() -> question.setKnowledgeAreas(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Knowledge areas must not be null or empty.");
        assertThatThrownBy(() -> question.setKnowledgeAreas(Set.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Knowledge areas must not be null or empty.");

        Set<KnowledgeArea> areasWithNull = new HashSet<>();
        areasWithNull.add(KnowledgeArea.DATABASE);
        areasWithNull.add(null);

        assertThatThrownBy(() -> question.setKnowledgeAreas(areasWithNull))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Knowledge areas must not contain null values.");
    }

    @Test
    void shouldRejectInvalidKnowledgeAreasInConstructor() {
        assertThatThrownBy(() -> new ProjectQuestion(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Build an API",
                "Build a REST API with persistence and tests.",
                Set.of(),
                DifficultyLevel.MEDIUM,
                RelevanceLevel.FOUR,
                LocalDateTime.of(2026, 6, 18, 10, 0),
                LocalDateTime.of(2026, 6, 25, 10, 0),
                QuestionStatus.VOTING,
                RelevanceLevel.THREE,
                0
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Knowledge areas must not be null or empty.");
    }

    @Test
    void shouldRejectNegativeRecruiterUsageCount() {
        ProjectQuestion question = new ProjectQuestion();

        assertThatThrownBy(() -> question.setRecruiterUsageCount(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Recruiter usage count must not be negative.");
    }

    @Test
    void shouldRejectNegativeRecruiterUsageCountInConstructor() {
        assertThatThrownBy(() -> new ProjectQuestion(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Build an API",
                "Build a REST API with persistence and tests.",
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                DifficultyLevel.MEDIUM,
                RelevanceLevel.FOUR,
                LocalDateTime.of(2026, 6, 18, 10, 0),
                LocalDateTime.of(2026, 6, 25, 10, 0),
                QuestionStatus.VOTING,
                RelevanceLevel.THREE,
                -1
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Recruiter usage count must not be negative.");
    }
}
