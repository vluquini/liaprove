package com.lia.liaprove.infrastructure.controllers.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.core.domain.assessment.AssessmentAttemptStatus;
import com.lia.liaprove.core.usecases.assessments.ListAllAssessmentAttemptsUseCase;
import com.lia.liaprove.infrastructure.dtos.assessment.AssessmentAttemptSummaryResponse;
import com.lia.liaprove.infrastructure.mappers.assessment.AssessmentDtoMapper;
import com.lia.liaprove.infrastructure.security.SecurityContextService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/assessments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAssessmentController {

    private final ListAllAssessmentAttemptsUseCase listAllAssessmentAttemptsUseCase;
    private final SecurityContextService securityContextService;
    private final AssessmentDtoMapper assessmentDtoMapper;

    public AdminAssessmentController(
            ListAllAssessmentAttemptsUseCase listAllAssessmentAttemptsUseCase,
            SecurityContextService securityContextService,
            AssessmentDtoMapper assessmentDtoMapper
    ) {
        this.listAllAssessmentAttemptsUseCase = listAllAssessmentAttemptsUseCase;
        this.securityContextService = securityContextService;
        this.assessmentDtoMapper = assessmentDtoMapper;
    }

    @GetMapping("/attempts")
    public ResponseEntity<List<AssessmentAttemptSummaryResponse>> listAllAttempts(
            @RequestParam(required = false) Boolean isPersonalized,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Set<AssessmentAttemptStatus> statuses
    ) {
        UUID requesterId = securityContextService.getCurrentUserId();

        List<AssessmentAttempt> attempts = listAllAssessmentAttemptsUseCase.execute(
                requesterId,
                Optional.ofNullable(isPersonalized),
                Optional.ofNullable(startDate),
                Optional.ofNullable(endDate),
                Optional.ofNullable(statuses)
        );

        List<AssessmentAttemptSummaryResponse> response = attempts.stream()
                .map(assessmentDtoMapper::toAttemptSummaryResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
