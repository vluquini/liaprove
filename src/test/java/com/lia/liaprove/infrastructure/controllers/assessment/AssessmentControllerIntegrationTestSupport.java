package com.lia.liaprove.infrastructure.controllers.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.infrastructure.entities.assessment.AnswerEntity;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.question.OpenQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.entities.user.UserRecruiterEntity;
import com.lia.liaprove.infrastructure.repositories.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

final class AssessmentControllerIntegrationTestSupport {

    static final String DEV_USER_HEADER = "X-Dev-User-Email";
    static final String CANDIDATE_EMAIL = "carlos.silva@example.com";
    static final String RECRUITER_EMAIL = "ana.p@techrecruit.com";
    static final String OTHER_RECRUITER_EMAIL = "roberto.l@hiredev.com";
    static final String ADMIN_EMAIL = "admin@liaprove.com";
    static final UUID SEEDED_MULTIPLE_CHOICE_QUESTION_ID =
            UUID.fromString("00000001-0000-0000-0000-000000000001");

    private AssessmentControllerIntegrationTestSupport() {
    }

    static UserEntity getSeededUser(UserJpaRepository userJpaRepository, String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Seeded user not found: " + email));
    }

    static UserRecruiterEntity getSeededRecruiter(UserJpaRepository userJpaRepository, String email) {
        return (UserRecruiterEntity) getSeededUser(userJpaRepository, email);
    }

    static QuestionEntity getSeededMultipleChoiceQuestion(QuestionJpaRepository questionJpaRepository) {
        return questionJpaRepository.findById(SEEDED_MULTIPLE_CHOICE_QUESTION_ID)
                .orElseThrow(() -> new IllegalStateException("Seeded question not found: " + SEEDED_MULTIPLE_CHOICE_QUESTION_ID));
    }

    static PersonalizedAssessmentEntity createPersonalizedAssessment(
            AssessmentJpaRepository assessmentJpaRepository,
            UserEntity recruiter
    ) {
        PersonalizedAssessmentEntity assessment = new PersonalizedAssessmentEntity();
        assessment.setTitle("Test " + UUID.randomUUID());
        assessment.setDescription("desc");
        assessment.setCreationDate(LocalDateTime.now());
        assessment.setCreatedBy((UserRecruiterEntity) recruiter);
        assessment.setShareableToken("token-" + UUID.randomUUID());
        assessment.setStatus(PersonalizedAssessmentStatus.ACTIVE);
        assessment.setMaxAttempts(3);
        assessment.setEvaluationTimerSeconds(1800L);
        return assessmentJpaRepository.save(assessment);
    }

    static OpenQuestionEntity createOpenQuestion(
            QuestionJpaRepository questionJpaRepository,
            UserRecruiterEntity recruiter,
            String title
    ) {
        OpenQuestionEntity question = new OpenQuestionEntity();
        question.setAuthorId(recruiter.getId());
        question.setTitle(title);
        question.setDescription("Describe the tradeoffs considered in your solution. " + UUID.randomUUID());
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setRelevanceByLLM(RelevanceLevel.THREE);
        question.setSubmissionDate(LocalDateTime.now());
        question.setVotingEndDate(LocalDateTime.now().plusDays(7));
        question.setStatus(QuestionStatus.FINISHED);
        question.setRecruiterUsageCount(0);
        question.setGuideline("Describe the technical decision and the tradeoffs involved.");
        question.setVisibility(OpenQuestionVisibility.SHARED);
        return (OpenQuestionEntity) questionJpaRepository.save(question);
    }

    static void applyJobDescriptionAnalysis(PersonalizedAssessmentEntity assessment) {
        assessment.setHardSkillsWeight(50);
        assessment.setSoftSkillsWeight(30);
        assessment.setExperienceWeight(20);
        assessment.setOriginalJobDescription("Senior backend engineer with strong Java and communication skills.");
        assessment.setSuggestedKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT, KnowledgeArea.AI));
        assessment.setSuggestedHardSkills(List.of("Java", "Spring Boot"));
        assessment.setSuggestedSoftSkills(List.of("Communication", "Teamwork"));
        assessment.setSuggestedHardSkillsWeight(45);
        assessment.setSuggestedSoftSkillsWeight(35);
        assessment.setSuggestedExperienceWeight(20);
    }

    static AssessmentAttemptEntity createAttempt(
            AssessmentAttemptJpaRepository assessmentAttemptJpaRepository,
            PersonalizedAssessmentEntity assessment,
            UserEntity candidate,
            AssessmentAttemptStatus status
    ) {
        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity();
        attempt.setAssessment(assessment);
        attempt.setUser(candidate);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setStatus(status);
        return assessmentAttemptJpaRepository.save(attempt);
    }

    static AssessmentAttemptEntity createAttemptWithOpenQuestionAnswer(
            AssessmentAttemptJpaRepository assessmentAttemptJpaRepository,
            PersonalizedAssessmentEntity assessment,
            UserEntity candidate,
            OpenQuestionEntity openQuestion,
            String textResponse
    ) {
        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity();
        attempt.setAssessment(assessment);
        attempt.setUser(candidate);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setStatus(AssessmentAttemptStatus.COMPLETED);
        attempt.setQuestions(List.of(openQuestion));

        AnswerEntity answer = new AnswerEntity();
        answer.setQuestionId(openQuestion.getId());
        answer.setTextResponse(textResponse);
        attempt.addAnswer(answer);

        return assessmentAttemptJpaRepository.save(attempt);
    }

    static void deleteAssessmentData(
            AssessmentAttemptJpaRepository assessmentAttemptJpaRepository,
            AssessmentJpaRepository assessmentJpaRepository,
            UserJpaRepository userJpaRepository
    ) {
        assessmentAttemptJpaRepository.deleteAll();
        assessmentJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }
}
