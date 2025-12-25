package com.lia.liaprove.infrastructure.controllers;

import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.application.services.question.QuestionCreateDto;
import com.lia.liaprove.core.usecases.question.*;
import com.lia.liaprove.infrastructure.dtos.question.MultipleChoiceQuestionRequest;
import com.lia.liaprove.infrastructure.dtos.question.ProjectQuestionRequest;
import com.lia.liaprove.infrastructure.dtos.question.QuestionRequest;
import com.lia.liaprove.infrastructure.dtos.question.QuestionResponse;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final SubmitQuestionUseCase submitQuestionUseCase;
    private final QuestionMapper questionMapper;

    @PostMapping
    public ResponseEntity<QuestionResponse> submitQuestion(@Valid @RequestBody QuestionRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UUID authorId = principal.user().getId();

        // Dispatch pela subclasse do DTO de request
        Question submitted;

        if (request instanceof MultipleChoiceQuestionRequest mcReq) {
            // mapear CreateMultipleChoiceQuestionRequest -> QuestionCreateDto
            QuestionCreateDto dto = questionMapper.toQuestionCreateDto(mcReq, authorId);
            // validação adicional de negócio pode acontecer no use case
            submitted = submitQuestionUseCase.createMultipleChoice(dto);
        } else if (request instanceof ProjectQuestionRequest pReq) {
            QuestionCreateDto dto = questionMapper.toQuestionCreateDto(pReq, authorId);
            submitted = submitQuestionUseCase.createProject(dto);
        } else {
            return ResponseEntity.badRequest().build(); // ou lançar IllegalArgumentException
        }

        QuestionResponse responseDto = questionMapper.toResponseDto(submitted);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}

