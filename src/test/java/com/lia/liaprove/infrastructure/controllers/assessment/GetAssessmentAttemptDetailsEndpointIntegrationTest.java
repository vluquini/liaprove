package com.lia.liaprove.infrastructure.controllers.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.infrastructure.entities.assessment.AnswerEntity;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.entities.assessment.PersonalizedAssessmentEntity;
import com.lia.liaprove.infrastructure.entities.question.OpenQuestionEntity;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.entities.user.UserRecruiterEntity;
import com.lia.liaprove.infrastructure.repositories.AssessmentAttemptJpaRepository;
import com.lia.liaprove.infrastructure.repositories.AssessmentJpaRepository;
import com.lia.liaprove.infrastructure.repositories.QuestionJpaRepository;
import com.lia.liaprove.infrastructure.repositories.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.CANDIDATE_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.DEV_USER_HEADER;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.RECRUITER_EMAIL;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.applyJobDescriptionAnalysis;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.createAttempt;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.createAttemptWithOpenQuestionAnswer;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.createOpenQuestion;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.createPersonalizedAssessment;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.deleteAssessmentData;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.getSeededRecruiter;
import static com.lia.liaprove.infrastructure.controllers.assessment.AssessmentControllerIntegrationTestSupport.getSeededUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = {
        "classpath:db/h2-populate-users.sql",
        "classpath:db/h2-populate-questions.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class GetAssessmentAttemptDetailsEndpointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private QuestionJpaRepository questionJpaRepository;

    @Autowired
    private AssessmentJpaRepository assessmentJpaRepository;

    @Autowired
    private AssessmentAttemptJpaRepository assessmentAttemptJpaRepository;

    @AfterEach
    void tearDown() {
        deleteAssessmentData(assessmentAttemptJpaRepository, assessmentJpaRepository, userJpaRepository);
    }

    @Test
    @DisplayName("Should get attempt details successfully")
    void shouldGetAttemptDetailsSuccessfully() throws Exception {
        UserEntity recruiter = getSeededUser(userJpaRepository, RECRUITER_EMAIL);
        UserEntity candidate = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);
        PersonalizedAssessmentEntity assessment = createPersonalizedAssessment(assessmentJpaRepository, recruiter);
        AssessmentAttemptEntity attempt = createAttempt(
                assessmentAttemptJpaRepository,
                assessment,
                candidate,
                AssessmentAttemptStatus.COMPLETED
        );

        mockMvc.perform(get("/api/v1/assessments/attempts/" + attempt.getId())
                        .header(DEV_USER_HEADER, recruiter.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attemptId").value(attempt.getId().toString()));
    }

    @Test
    @DisplayName("Should return text response in attempt details when personalized assessment contains open question")
    void shouldReturnTextResponseInAttemptDetailsWhenPersonalizedAssessmentContainsOpenQuestion() throws Exception {
        UserRecruiterEntity recruiter = getSeededRecruiter(userJpaRepository, RECRUITER_EMAIL);
        UserEntity candidate = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);
        OpenQuestionEntity openQuestion = createOpenQuestion(questionJpaRepository, recruiter, "Explain your architectural choice");

        PersonalizedAssessmentEntity assessment = createPersonalizedAssessment(assessmentJpaRepository, recruiter);
        assessment.setQuestions(List.of(openQuestion));
        applyJobDescriptionAnalysis(assessment);
        assessment = assessmentJpaRepository.save(assessment);

        AssessmentAttemptEntity attempt = createAttemptWithOpenQuestionAnswer(
                assessmentAttemptJpaRepository,
                assessment,
                candidate,
                openQuestion,
                "I prioritized maintainability over premature optimization."
        );

        mockMvc.perform(get("/api/v1/assessments/attempts/" + attempt.getId())
                        .header(DEV_USER_HEADER, recruiter.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assessment.criteriaWeights.hardSkillsWeight").value(50))
                .andExpect(jsonPath("$.assessment.criteriaWeights.softSkillsWeight").value(30))
                .andExpect(jsonPath("$.assessment.criteriaWeights.experienceWeight").value(20))
                .andExpect(jsonPath("$.assessment.jobDescriptionAnalysis.originalJobDescription")
                        .value("Senior backend engineer with strong Java and communication skills."))
                .andExpect(jsonPath("$.assessment.jobDescriptionAnalysis.suggestedHardSkills[0]").value("Java"))
                .andExpect(jsonPath("$.assessment.jobDescriptionAnalysis.suggestedCriteriaWeights.hardSkillsWeight").value(45))
                .andExpect(jsonPath("$.explainability.totalQuestions").value(1))
                .andExpect(jsonPath("$.explainability.answeredQuestions").value(1))
                .andExpect(jsonPath("$.explainability.openQuestions").value(1))
                .andExpect(jsonPath("$.explainability.multipleChoiceQuestions").value(0))
                .andExpect(jsonPath("$.explainability.projectQuestions").value(0))
                .andExpect(jsonPath("$.explainability.candidateExperienceLevel").value("SENIOR"))
                .andExpect(jsonPath("$.explainability.candidateHardSkills").isArray())
                .andExpect(jsonPath("$.explainability.criteriaWeights.hardSkillsWeight").value(50))
                .andExpect(jsonPath("$.questions[0].guideline")
                        .value("Describe the technical decision and the tradeoffs involved."))
                .andExpect(jsonPath("$.questions[0].answer.questionId").value(openQuestion.getId().toString()))
                .andExpect(jsonPath("$.questions[0].answer.textResponse")
                        .value("I prioritized maintainability over premature optimization."));
    }

    @Test
    @DisplayName("Should count answered questions once per unique question in attempt details")
    void shouldCountAnsweredQuestionsOncePerUniqueQuestionInAttemptDetails() throws Exception {
        UserRecruiterEntity recruiter = getSeededRecruiter(userJpaRepository, RECRUITER_EMAIL);
        UserEntity candidate = getSeededUser(userJpaRepository, CANDIDATE_EMAIL);
        OpenQuestionEntity openQuestion = createOpenQuestion(
                questionJpaRepository,
                recruiter,
                "Explain your architectural choice " + UUID.randomUUID()
        );

        PersonalizedAssessmentEntity assessment = createPersonalizedAssessment(assessmentJpaRepository, recruiter);
        assessment.setQuestions(List.of(openQuestion));
        applyJobDescriptionAnalysis(assessment);
        assessment = assessmentJpaRepository.save(assessment);

        AssessmentAttemptEntity attempt = new AssessmentAttemptEntity();
        attempt.setAssessment(assessment);
        attempt.setUser(candidate);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setStatus(AssessmentAttemptStatus.COMPLETED);
        attempt.setQuestions(List.of(openQuestion));

        AnswerEntity firstAnswer = new AnswerEntity();
        firstAnswer.setQuestionId(openQuestion.getId());
        firstAnswer.setTextResponse("First answer");
        attempt.addAnswer(firstAnswer);

        AnswerEntity duplicateAnswer = new AnswerEntity();
        duplicateAnswer.setQuestionId(openQuestion.getId());
        duplicateAnswer.setTextResponse("Duplicate answer");
        attempt.addAnswer(duplicateAnswer);

        attempt = assessmentAttemptJpaRepository.save(attempt);

        mockMvc.perform(get("/api/v1/assessments/attempts/" + attempt.getId())
                        .header(DEV_USER_HEADER, recruiter.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.explainability.answeredQuestions").value(1));
    }
}
