package com.lia.liaprove.infrastructure.tasks;

import com.lia.liaprove.core.domain.question.*;
import com.lia.liaprove.infrastructure.entities.question.ProjectQuestionEntity;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestEntityManager
@Transactional
class QuestionVotingEvaluatorSchedulerTest {

    @Autowired
    private QuestionVotingEvaluatorScheduler scheduler;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuestionMapper questionMapper;

    @Test
    void shouldUpdateStatusOfExpiredQuestionsOnly() {
        // Arrange
        // Question that should be processed
        ProjectQuestionEntity expiredQuestionEntity = createTestQuestionEntity("Expired Question", LocalDateTime.now().minusDays(1));
        entityManager.persistAndFlush(expiredQuestionEntity);

        // Question that should NOT be processed
        ProjectQuestionEntity activeQuestionEntity = createTestQuestionEntity("Active Question", LocalDateTime.now().plusDays(1));
        entityManager.persistAndFlush(activeQuestionEntity);

        // Detach all entities to ensure a clean state before the scheduler runs
        entityManager.clear();

        // Act
        scheduler.evaluateExpiredQuestionVotes();

        // Assert
        ProjectQuestionEntity processedQuestion = entityManager.find(ProjectQuestionEntity.class, expiredQuestionEntity.getId());
        assertThat(processedQuestion.getStatus()).isIn(QuestionStatus.APPROVED, QuestionStatus.REJECTED);

        ProjectQuestionEntity unprocessedQuestion = entityManager.find(ProjectQuestionEntity.class, activeQuestionEntity.getId());
        assertThat(unprocessedQuestion.getStatus()).isEqualTo(QuestionStatus.VOTING);
    }

    private ProjectQuestionEntity createTestQuestionEntity(String title, LocalDateTime votingEndDate) {
        ProjectQuestion questionDomain = new ProjectQuestion();
        questionDomain.setAuthorId(UUID.randomUUID());
        questionDomain.setTitle(title);
        questionDomain.setDescription("Description for " + title);
        questionDomain.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        questionDomain.setDifficultyByCommunity(DifficultyLevel.EASY);
        questionDomain.setRelevanceByCommunity(RelevanceLevel.THREE);
        questionDomain.setSubmissionDate(LocalDateTime.now().minusDays(10));
        questionDomain.setStatus(QuestionStatus.VOTING);
        questionDomain.setRelevanceByLLM(RelevanceLevel.FOUR);
        questionDomain.setRecruiterUsageCount(0);
        questionDomain.setVotingEndDate(votingEndDate);
        
        // Use mapper to create the entity from the domain object
        return questionMapper.toEntity(questionDomain);
    }
}