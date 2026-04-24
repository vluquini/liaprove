package com.lia.liaprove.infrastructure.controllers.metrics;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.infrastructure.dtos.metrics.ReactToFeedbackRequest;
import com.lia.liaprove.infrastructure.dtos.metrics.SubmitFeedbackQuestionRequest;
import com.lia.liaprove.infrastructure.entities.assessment.AnswerEntity;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.assessment.SystemAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.ProjectQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentAttemptVoteJpaRepository;
import com.lia.liaprove.infrastructure.repositories.assessment.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.metrics.FeedbackAssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.metrics.FeedbackQuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.question.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
import com.lia.liaprove.infrastructure.repositories.metrics.VoteJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.lia.liaprove.core.domain.metrics.ReactionType.LIKE;

final class MetricsControllerIntegrationTestSupport {

    static final String DEV_USER_HEADER = "X-Dev-User-Email";
    static final String PROFESSIONAL_EMAIL = "carlos.silva@example.com";
    static final String OTHER_PROFESSIONAL_EMAIL = "mariana.costa@example.com";
    static final String JUNIOR_EMAIL = "junior.dev@example.com";
    static final String ADMIN_EMAIL = "admin@liaprove.com";

    private MetricsControllerIntegrationTestSupport() {
    }

    static UserEntity getSeededUser(UserJpaRepository userJpaRepository, String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Seeded user not found: " + email));
    }

    static QuestionEntity createTestQuestion(QuestionJpaRepository questionJpaRepository, QuestionMapper questionMapper) {
        MultipleChoiceQuestion questionDomain = new MultipleChoiceQuestion(
                List.of(
                        new Alternative(null, "Alternative A", false),
                        new Alternative(null, "Alternative B", true)
                )
        );
        questionDomain.setAuthorId(UUID.randomUUID());
        questionDomain.setTitle("Test Question Title - " + UUID.randomUUID());
        questionDomain.setDescription("Test question description. - " + UUID.randomUUID());
        questionDomain.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        questionDomain.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        questionDomain.setStatus(QuestionStatus.VOTING);
        questionDomain.setSubmissionDate(LocalDateTime.now());
        questionDomain.setVotingEndDate(LocalDateTime.now().plusDays(7));
        questionDomain.setRelevanceByCommunity(RelevanceLevel.THREE);
        questionDomain.setRelevanceByLLM(RelevanceLevel.FOUR);
        questionDomain.setRecruiterUsageCount(0);

        return questionJpaRepository.save(questionMapper.toEntity(questionDomain));
    }

    static FeedbackQuestionEntity createTestFeedbackQuestion(
            FeedbackQuestionJpaRepository feedbackQuestionJpaRepository,
            UserEntity author,
            QuestionEntity question
    ) {
        FeedbackQuestionEntity feedback = new FeedbackQuestionEntity();
        feedback.setUser(author);
        feedback.setQuestion(question);
        feedback.setComment("This is a test feedback comment.");
        feedback.setSubmissionDate(LocalDateTime.now());
        return feedbackQuestionJpaRepository.save(feedback);
    }

    static AssessmentAttemptEntity createFinishedSystemProjectAttempt(
            QuestionJpaRepository questionJpaRepository,
            AssessmentJpaRepository assessmentJpaRepository,
            AssessmentAttemptJpaRepository assessmentAttemptJpaRepository,
            UserEntity owner,
            String repositoryLink
    ) {
        ProjectQuestionEntity projectQuestion = createProjectQuestionEntity(questionJpaRepository);

        SystemAssessmentEntity assessment = new SystemAssessmentEntity();
        assessment.setTitle("System Project Assessment " + UUID.randomUUID());
        assessment.setDescription("System project assessment description");
        assessment.setCreationDate(LocalDateTime.now().minusDays(1));
        assessment.setEvaluationTimerSeconds(3600L);
        assessment.setQuestions(List.of(projectQuestion));
        SystemAssessmentEntity savedAssessment = assessmentJpaRepository.save(assessment);

        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity();
        attempt.setAssessment(savedAssessment);
        attempt.setUser(owner);
        attempt.setQuestions(List.of(projectQuestion));
        attempt.setStartedAt(LocalDateTime.now().minusHours(2));
        attempt.setFinishedAt(LocalDateTime.now().minusHours(1));
        attempt.setStatus(AssessmentAttemptStatus.COMPLETED);
        attempt.setAccuracyRate(0);

        AnswerEntity answer = new AnswerEntity();
        answer.setQuestionId(projectQuestion.getId());
        answer.setProjectUrl(repositoryLink);
        attempt.addAnswer(answer);

        return assessmentAttemptJpaRepository.save(attempt);
    }

    static SubmitFeedbackQuestionRequest validQuestionFeedbackRequest() {
        SubmitFeedbackQuestionRequest request = new SubmitFeedbackQuestionRequest();
        request.setComment("This is a new feedback from an integration test.");
        request.setDifficultyLevel(DifficultyLevel.EASY);
        request.setKnowledgeArea(KnowledgeArea.AI);
        request.setRelevanceLevel(RelevanceLevel.FIVE);
        return request;
    }

    static SubmitFeedbackQuestionRequest invalidQuestionFeedbackRequest() {
        SubmitFeedbackQuestionRequest request = new SubmitFeedbackQuestionRequest();
        request.setComment("");
        request.setDifficultyLevel(DifficultyLevel.EASY);
        request.setKnowledgeArea(KnowledgeArea.AI);
        request.setRelevanceLevel(RelevanceLevel.FIVE);
        return request;
    }

    static ReactToFeedbackRequest likeReactionRequest() {
        ReactToFeedbackRequest request = new ReactToFeedbackRequest();
        request.setReactionType(LIKE);
        return request;
    }

    static void deleteMetricsData(
            AssessmentAttemptVoteJpaRepository assessmentAttemptVoteJpaRepository,
            AssessmentAttemptJpaRepository assessmentAttemptJpaRepository,
            VoteJpaRepository voteJpaRepository,
            FeedbackAssessmentJpaRepository feedbackAssessmentJpaRepository,
            FeedbackQuestionJpaRepository feedbackQuestionJpaRepository,
            AssessmentJpaRepository assessmentJpaRepository,
            QuestionJpaRepository questionJpaRepository,
            UserJpaRepository userJpaRepository
    ) {
        assessmentAttemptVoteJpaRepository.deleteAll();
        assessmentAttemptJpaRepository.deleteAll();
        voteJpaRepository.deleteAll();
        feedbackAssessmentJpaRepository.deleteAll();
        feedbackQuestionJpaRepository.deleteAll();
        assessmentJpaRepository.deleteAll();
        questionJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    private static ProjectQuestionEntity createProjectQuestionEntity(QuestionJpaRepository questionJpaRepository) {
        ProjectQuestionEntity question = new ProjectQuestionEntity();
        question.setAuthorId(UUID.randomUUID());
        question.setTitle("Project Question " + UUID.randomUUID());
        question.setDescription("Project question description " + UUID.randomUUID());
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setSubmissionDate(LocalDateTime.now().minusDays(2));
        question.setVotingEndDate(LocalDateTime.now().plusDays(2));
        question.setStatus(QuestionStatus.APPROVED);
        question.setRelevanceByLLM(RelevanceLevel.FOUR);
        question.setRecruiterUsageCount(0);
        return questionJpaRepository.save(question);
    }
}
