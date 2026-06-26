package com.lia.liaprove.infrastructure.controllers.assessment;

import com.lia.liaprove.application.services.assessment.dto.SuggestionCriteriaDto;
import com.lia.liaprove.core.domain.assessment.AssessmentCriteriaWeights;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessment;
import com.lia.liaprove.core.domain.assessment.PersonalizedAssessmentStatus;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.usecases.assessments.AnalyzeJobDescriptionUseCase;
import com.lia.liaprove.core.usecases.assessments.CreatePersonalizedAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.DeletePersonalizedAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.EvaluateAssessmentAttemptUseCase;
import com.lia.liaprove.core.usecases.assessments.GenerateAttemptPreAnalysisUseCase;
import com.lia.liaprove.core.usecases.assessments.GetAssessmentAttemptDetailsUseCase;
import com.lia.liaprove.core.usecases.assessments.GetPersonalizedAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.ListAttemptsForMyAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.ListPersonalizedAssessmentsUseCase;
import com.lia.liaprove.core.usecases.assessments.StartNewAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.SubmitAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.SuggestQuestionsForAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.UpdatePersonalizedAssessmentUseCase;
import com.lia.liaprove.infrastructure.dtos.assessment.PersonalizedAssessmentDetailsResponse;
import com.lia.liaprove.infrastructure.mappers.assessment.AssessmentDtoMapper;
import com.lia.liaprove.infrastructure.mappers.user.UserMapper;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;
import com.lia.liaprove.infrastructure.security.SecurityContextService;
import com.lia.liaprove.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssessmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AssessmentControllerSuggestionWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StartNewAssessmentUseCase startNewAssessmentUseCase;

    @MockitoBean
    private SubmitAssessmentUseCase submitAssessmentUseCase;

    @MockitoBean
    private CreatePersonalizedAssessmentUseCase createPersonalizedAssessmentUseCase;

    @MockitoBean
    private SuggestQuestionsForAssessmentUseCase suggestQuestionsForAssessmentUseCase;

    @MockitoBean
    private EvaluateAssessmentAttemptUseCase evaluateAssessmentAttemptUseCase;

    @MockitoBean
    private DeletePersonalizedAssessmentUseCase deletePersonalizedAssessmentUseCase;

    @MockitoBean
    private GetAssessmentAttemptDetailsUseCase getAssessmentAttemptDetailsUseCase;

    @MockitoBean
    private GenerateAttemptPreAnalysisUseCase generateAttemptPreAnalysisUseCase;

    @MockitoBean
    private ListAttemptsForMyAssessmentUseCase listAttemptsForMyAssessmentUseCase;

    @MockitoBean
    private ListPersonalizedAssessmentsUseCase listPersonalizedAssessmentsUseCase;

    @MockitoBean
    private GetPersonalizedAssessmentUseCase getPersonalizedAssessmentUseCase;

    @MockitoBean
    private UpdatePersonalizedAssessmentUseCase updatePersonalizedAssessmentUseCase;

    @MockitoBean
    private AnalyzeJobDescriptionUseCase analyzeJobDescriptionUseCase;

    @MockitoBean
    private SecurityContextService securityContextService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserJpaRepository userJpaRepository;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private AssessmentDtoMapper assessmentDtoMapper;

    @Test
    void shouldListPersonalizedAssessmentsForCurrentRequester() throws Exception {
        UUID requesterId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        PersonalizedAssessment assessment = assessment(assessmentId);
        PersonalizedAssessmentDetailsResponse response = response(assessmentId, "Recruiter assessment");

        when(securityContextService.getCurrentUserId()).thenReturn(requesterId);
        when(listPersonalizedAssessmentsUseCase.execute(requesterId)).thenReturn(List.of(assessment));
        when(assessmentDtoMapper.toPersonalizedDetailsResponse(assessment)).thenReturn(response);

        mockMvc.perform(get("/api/v1/assessments/personalized"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(assessmentId.toString()))
                .andExpect(jsonPath("$[0].title").value("Recruiter assessment"))
                .andExpect(jsonPath("$[0].shareableToken").value("token-1"));

        verify(listPersonalizedAssessmentsUseCase).execute(requesterId);
        verify(assessmentDtoMapper).toPersonalizedDetailsResponse(assessment);
    }

    @Test
    void shouldGetPersonalizedAssessmentForCurrentRequester() throws Exception {
        UUID requesterId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        PersonalizedAssessment assessment = assessment(assessmentId);
        PersonalizedAssessmentDetailsResponse response = response(assessmentId, "Detail assessment");

        when(securityContextService.getCurrentUserId()).thenReturn(requesterId);
        when(getPersonalizedAssessmentUseCase.execute(assessmentId, requesterId)).thenReturn(assessment);
        when(assessmentDtoMapper.toPersonalizedDetailsResponse(assessment)).thenReturn(response);

        mockMvc.perform(get("/api/v1/assessments/personalized/{assessmentId}", assessmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(assessmentId.toString()))
                .andExpect(jsonPath("$.title").value("Detail assessment"));

        verify(getPersonalizedAssessmentUseCase).execute(assessmentId, requesterId);
        verify(assessmentDtoMapper).toPersonalizedDetailsResponse(assessment);
    }

    @Test
    void attemptsEndpointShouldAllowRecruiterAndAdminRoles() throws Exception {
        Method method = AssessmentController.class.getDeclaredMethod("listAttemptsForMyAssessment", UUID.class);

        PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);

        assertThat(preAuthorize.value()).isEqualTo("hasAnyRole('RECRUITER', 'ADMIN')");
    }

    @Test
    void shouldBindQuestionTypesFilterIntoSuggestionCriteria() throws Exception {
        UUID recruiterId = UUID.randomUUID();
        when(securityContextService.getCurrentUserId()).thenReturn(recruiterId);
        when(suggestQuestionsForAssessmentUseCase.execute(eq(recruiterId), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/assessments/personalized/suggestions")
                        .param("questionTypes", "OPEN")
                        .param("page", "2")
                        .param("pageSize", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());

        var criteriaCaptor = org.mockito.ArgumentCaptor.forClass(SuggestionCriteriaDto.class);
        verify(suggestQuestionsForAssessmentUseCase).execute(eq(recruiterId), criteriaCaptor.capture());

        SuggestionCriteriaDto criteria = criteriaCaptor.getValue();
        assertThat(criteria.getQuestionTypes()).isPresent();
        assertThat(criteria.getQuestionTypes().orElseThrow()).containsExactly(QuestionType.OPEN);
        assertThat(criteria.getPage()).isEqualTo(2);
        assertThat(criteria.getPageSize()).isEqualTo(7);
    }

    @Test
    void shouldLeaveQuestionTypesEmptyWhenFilterIsOmitted() throws Exception {
        UUID recruiterId = UUID.randomUUID();
        when(securityContextService.getCurrentUserId()).thenReturn(recruiterId);
        when(suggestQuestionsForAssessmentUseCase.execute(eq(recruiterId), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/assessments/personalized/suggestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        var criteriaCaptor = org.mockito.ArgumentCaptor.forClass(SuggestionCriteriaDto.class);
        verify(suggestQuestionsForAssessmentUseCase).execute(eq(recruiterId), criteriaCaptor.capture());

        assertThat(criteriaCaptor.getValue().getQuestionTypes()).isEmpty();
    }

    private PersonalizedAssessment assessment(UUID assessmentId) {
        return new PersonalizedAssessment(
                assessmentId,
                "Assessment",
                "Description",
                LocalDateTime.now(),
                List.of(),
                Duration.ofMinutes(60),
                null,
                LocalDateTime.now().plusDays(7),
                3,
                "token-1",
                PersonalizedAssessmentStatus.ACTIVE,
                AssessmentCriteriaWeights.defaultWeights()
        );
    }

    private PersonalizedAssessmentDetailsResponse response(UUID assessmentId, String title) {
        return new PersonalizedAssessmentDetailsResponse(
                assessmentId,
                title,
                "Description",
                LocalDateTime.now(),
                60L,
                LocalDateTime.now().plusDays(7),
                0,
                3,
                "token-1",
                PersonalizedAssessmentStatus.ACTIVE,
                null,
                null,
                null,
                List.of()
        );
    }
}
