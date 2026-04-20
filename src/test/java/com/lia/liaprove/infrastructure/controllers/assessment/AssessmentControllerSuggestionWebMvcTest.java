package com.lia.liaprove.infrastructure.controllers.assessment;

import com.lia.liaprove.application.gateways.user.UserGateway;
import com.lia.liaprove.application.services.assessment.dto.SuggestionCriteriaDto;
import com.lia.liaprove.core.algorithms.bayesian.ScoredQuestion;
import com.lia.liaprove.core.domain.question.QuestionType;
import com.lia.liaprove.core.usecases.assessments.CreatePersonalizedAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.DeletePersonalizedAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.EvaluateAssessmentAttemptUseCase;
import com.lia.liaprove.core.usecases.assessments.GetAssessmentAttemptDetailsUseCase;
import com.lia.liaprove.core.usecases.assessments.ListAttemptsForMyAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.StartNewAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.SubmitAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.SuggestQuestionsForAssessmentUseCase;
import com.lia.liaprove.core.usecases.assessments.UpdatePersonalizedAssessmentUseCase;
import com.lia.liaprove.infrastructure.controllers.AssessmentController;
import com.lia.liaprove.infrastructure.mappers.assessment.AssessmentDtoMapper;
import com.lia.liaprove.infrastructure.security.SecurityContextService;
import com.lia.liaprove.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
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
    private ListAttemptsForMyAssessmentUseCase listAttemptsForMyAssessmentUseCase;

    @MockitoBean
    private UpdatePersonalizedAssessmentUseCase updatePersonalizedAssessmentUseCase;

    @MockitoBean
    private SecurityContextService securityContextService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AssessmentDtoMapper assessmentDtoMapper;

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
}
