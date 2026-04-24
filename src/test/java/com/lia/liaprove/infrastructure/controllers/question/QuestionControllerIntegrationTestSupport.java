package com.lia.liaprove.infrastructure.controllers.question;

import com.lia.liaprove.core.domain.question.DifficultyLevel;
import com.lia.liaprove.core.domain.question.KnowledgeArea;
import com.lia.liaprove.core.domain.question.OpenQuestionVisibility;
import com.lia.liaprove.core.domain.question.RelevanceLevel;
import com.lia.liaprove.infrastructure.dtos.question.AlternativeRequestDto;
import com.lia.liaprove.infrastructure.dtos.question.CreateOpenQuestionRequest;
import com.lia.liaprove.infrastructure.dtos.question.SubmitMultipleChoiceQuestionRequest;
import com.lia.liaprove.infrastructure.entities.user.UserEntity;
import com.lia.liaprove.infrastructure.repositories.user.UserJpaRepository;

import java.util.List;
import java.util.Set;

final class QuestionControllerIntegrationTestSupport {

    static final String DEV_USER_HEADER = "X-Dev-User-Email";
    static final String PROFESSIONAL_EMAIL = "carlos.silva@example.com";
    static final String RECRUITER_EMAIL = "ana.p@techrecruit.com";
    static final String ADMIN_EMAIL = "admin@liaprove.com";

    private QuestionControllerIntegrationTestSupport() {
    }

    static UserEntity getSeededUser(UserJpaRepository userJpaRepository, String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Seeded user not found: " + email));
    }

    static SubmitMultipleChoiceQuestionRequest validMultipleChoiceRequest() {
        SubmitMultipleChoiceQuestionRequest request = new SubmitMultipleChoiceQuestionRequest();
        request.setTitle("New Question Title with minimum length");
        request.setDescription("New question description that meets the minimum length requirement.");
        request.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        request.setDifficultyByCommunity(DifficultyLevel.EASY);
        request.setRelevanceByCommunity(RelevanceLevel.THREE);
        request.setAlternatives(List.of(
                new AlternativeRequestDto("Correct Answer Text", true),
                new AlternativeRequestDto("Wrong Answer Text 1", false),
                new AlternativeRequestDto("Wrong Answer Text 2", false)
        ));
        return request;
    }

    static SubmitMultipleChoiceQuestionRequest invalidMultipleChoiceRequest() {
        SubmitMultipleChoiceQuestionRequest request = new SubmitMultipleChoiceQuestionRequest();
        request.setTitle("short");
        request.setDescription("too short");
        request.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        request.setDifficultyByCommunity(DifficultyLevel.EASY);
        request.setRelevanceByCommunity(RelevanceLevel.THREE);
        request.setAlternatives(List.of(
                new AlternativeRequestDto("A", true),
                new AlternativeRequestDto("B", false),
                new AlternativeRequestDto("C", false)
        ));
        return request;
    }

    static CreateOpenQuestionRequest validOpenQuestionRequest(OpenQuestionVisibility visibility) {
        CreateOpenQuestionRequest request = new CreateOpenQuestionRequest();
        request.setTitle("Explain the open question creation flow");
        request.setDescription("Describe how recruiters create open questions and how they are persisted.");
        request.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        request.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        request.setRelevanceByCommunity(RelevanceLevel.FOUR);
        request.setGuideline("Mention the expected answer structure and scoring hints.");
        request.setVisibility(visibility);
        return request;
    }

    static CreateOpenQuestionRequest invalidOpenQuestionRequest() {
        CreateOpenQuestionRequest request = new CreateOpenQuestionRequest();
        request.setTitle("short");
        request.setDescription("too short");
        request.setKnowledgeAreas(Set.of(KnowledgeArea.SOFTWARE_DEVELOPMENT));
        request.setDifficultyByCommunity(DifficultyLevel.MEDIUM);
        request.setRelevanceByCommunity(RelevanceLevel.FOUR);
        request.setGuideline("short");
        return request;
    }
}
