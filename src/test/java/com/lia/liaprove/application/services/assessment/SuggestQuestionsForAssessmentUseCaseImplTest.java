package com.lia.liaprove.application.services.assessment;

import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.application.services.assessment.dto.SuggestionCriteriaDto;
import com.lia.liaprove.core.algorithms.bayesian.ScoredQuestion;
import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.OpenQuestion;
import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.core.domain.question.QuestionStatus;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.core.domain.user.ExperienceLevel;
import com.lia.liaprove.core.domain.user.UserRecruiter;
import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.core.domain.user.UserStatus;
import com.lia.liaprove.core.usecases.algorithms.bayesian.BayesianNetworkUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SuggestQuestionsForAssessmentUseCaseImplTest {

    @Mock
    private BayesianNetworkUseCase bayesianNetworkUseCase;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private SuggestQuestionsForAssessmentUseCaseImpl useCase;

    @Test
    void shouldFilterOpenQuestionsByTypeAndVisibility() {
        UUID recruiterId = UUID.randomUUID();
        UserRecruiter recruiter = recruiter(recruiterId);

        Question ownPrivateOpenQuestion = openQuestion(
                UUID.randomUUID(),
                recruiterId,
                OpenQuestionVisibility.PRIVATE,
                "Own private open question"
        );
        Question ownSharedOpenQuestion = openQuestion(
                UUID.randomUUID(),
                recruiterId,
                OpenQuestionVisibility.SHARED,
                "Own shared open question"
        );
        Question otherSharedOpenQuestion = openQuestion(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OpenQuestionVisibility.SHARED,
                "Other shared open question"
        );
        Question otherPrivateOpenQuestion = openQuestion(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OpenQuestionVisibility.PRIVATE,
                "Other private open question"
        );
        Question multipleChoiceQuestion = multipleChoiceQuestion();
        Question projectQuestion = projectQuestion();

        when(userGateway.findById(recruiterId)).thenReturn(Optional.of(recruiter));
        when(bayesianNetworkUseCase.suggestQuestionsForRecruiter(recruiterId, 200)).thenReturn(List.of(
                scored(multipleChoiceQuestion, 0.95),
                scored(otherPrivateOpenQuestion, 0.90),
                scored(projectQuestion, 0.85),
                scored(otherSharedOpenQuestion, 0.80),
                scored(ownSharedOpenQuestion, 0.75),
                scored(ownPrivateOpenQuestion, 0.70)
        ));

        SuggestionCriteriaDto criteria = new SuggestionCriteriaDto(
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                Set.of(DifficultyLevel.MEDIUM),
                Set.of(QuestionType.OPEN),
                1,
                10,
                List.of()
        );

        List<ScoredQuestion> suggestions = useCase.execute(recruiterId, criteria);

        assertThat(suggestions)
                .extracting(sq -> sq.getQuestion().getTitle())
                .containsExactly(
                        "Other shared open question",
                        "Own shared open question",
                        "Own private open question"
                );
    }

    @Test
    void shouldKeepNonOpenQuestionTypesAndExcludeOpenQuestionsWhenFiltered() {
        UUID recruiterId = UUID.randomUUID();
        UserRecruiter recruiter = recruiter(recruiterId);

        Question multipleChoiceQuestion = multipleChoiceQuestion();
        Question projectQuestion = projectQuestion();
        Question openQuestion = openQuestion(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OpenQuestionVisibility.SHARED,
                "Shared open question"
        );

        when(userGateway.findById(recruiterId)).thenReturn(Optional.of(recruiter));
        when(bayesianNetworkUseCase.suggestQuestionsForRecruiter(recruiterId, 200)).thenReturn(List.of(
                scored(openQuestion, 0.90),
                scored(projectQuestion, 0.80),
                scored(multipleChoiceQuestion, 0.70)
        ));

        SuggestionCriteriaDto criteria = new SuggestionCriteriaDto(
                Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT),
                Set.of(DifficultyLevel.MEDIUM),
                Set.of(QuestionType.MULTIPLE_CHOICE, QuestionType.PROJECT),
                1,
                10,
                List.of()
        );

        List<ScoredQuestion> suggestions = useCase.execute(recruiterId, criteria);

        assertThat(suggestions)
                .extracting(sq -> sq.getQuestion().getQuestionType())
                .containsExactly(QuestionType.PROJECT, QuestionType.MULTIPLE_CHOICE);
    }

    private UserRecruiter recruiter(UUID recruiterId) {
        UserRecruiter recruiter = new UserRecruiter(
                recruiterId,
                "Recruiter",
                "recruiter@example.com",
                "hashed-password",
                "Recruiter",
                "Bio",
                ExperienceLevel.SENIOR,
                UserRole.RECRUITER,
                5,
                0,
                List.of(),
                0.0f,
                LocalDateTime.now(),
                LocalDateTime.now(),
                UserStatus.ACTIVE
        );
        recruiter.setCompanyEmail("recruiter@example.com");
        recruiter.setCompanyName("Acme");
        return recruiter;
    }

    private ScoredQuestion scored(Question question, double score) {
        return new ScoredQuestion(question, score);
    }

    private OpenQuestion openQuestion(UUID id, UUID authorId, OpenQuestionVisibility visibility, String title) {
        OpenQuestion question = new OpenQuestion("Guideline for " + title, visibility);
        question.setId(id);
        question.setAuthorId(authorId);
        question.setTitle(title);
        question.setDescription(title + " description");
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setRelevanceByLLM(RelevanceLevel.THREE);
        question.setSubmissionDate(LocalDateTime.now());
        question.setVotingEndDate(LocalDateTime.now().plusDays(1));
        question.setStatus(QuestionStatus.FINISHED);
        return question;
    }

    private Question multipleChoiceQuestion() {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion();
        question.setId(UUID.randomUUID());
        question.setAuthorId(UUID.randomUUID());
        question.setTitle("Multiple choice question");
        question.setDescription("Multiple choice question description");
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setRelevanceByLLM(RelevanceLevel.THREE);
        question.setSubmissionDate(LocalDateTime.now());
        question.setVotingEndDate(LocalDateTime.now().plusDays(1));
        question.setStatus(QuestionStatus.FINISHED);
        return question;
    }

    private Question projectQuestion() {
        ProjectQuestion question = new ProjectQuestion();
        question.setId(UUID.randomUUID());
        question.setAuthorId(UUID.randomUUID());
        question.setTitle("Project question");
        question.setDescription("Project question description");
        question.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        question.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        question.setRelevanceByCommunity(RelevanceLevel.THREE);
        question.setRelevanceByLLM(RelevanceLevel.THREE);
        question.setSubmissionDate(LocalDateTime.now());
        question.setVotingEndDate(LocalDateTime.now().plusDays(1));
        question.setStatus(QuestionStatus.FINISHED);
        return question;
    }
}
