package com.lia.liaprove.infrastructure.mappers.metrics;

import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;
import com.lia.liaprove.core.domain.metrics.FeedbackReaction;
import com.lia.liaprove.infrastructure.dtos.metrics.FeedbackQuestionResponse;
import com.lia.liaprove.infrastructure.dtos.metrics.FeedbackReactionResponse;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackQuestionEntity;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackReactionEntity;
import com.lia.liaprove.infrastructure.mappers.users.UserMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface FeedbackQuestionMapper {

    // === FeedbackQuestion Mappings ===

    @Mapping(target = "reactions", ignore = true) // Managed manually in the gateway
    @Mapping(target = "question", ignore = true)
    FeedbackQuestionEntity toEntity(FeedbackQuestion domain);

    @Mapping(target = "reactions", ignore = true)
    @Mapping(target = "question", ignore = true)
    FeedbackQuestion toDomain(FeedbackQuestionEntity entity);

    @AfterMapping
    default void populateReactions(FeedbackQuestionEntity entity, @MappingTarget FeedbackQuestion domain) {
        if (entity != null && entity.getReactions() != null) {
            domain.setReactions(reactionEntityListToDomainList(entity.getReactions()));
        }
    }

    // === FeedbackReaction Mappings ===

    List<FeedbackReaction> reactionEntityListToDomainList(List<FeedbackReactionEntity> entities);

    @Mapping(target = "feedbackQuestion", ignore = true) // Set manually in the gateway
    FeedbackReactionEntity reactionToEntity(FeedbackReaction domain);

    FeedbackReaction reactionToDomain(FeedbackReactionEntity entity);

    // === List/Map Converters for nested collections ===

    default Map<UUID, FeedbackReaction> reactionEntityListToDomainMap(List<FeedbackReactionEntity> list) {
        if (list == null) {
            return new LinkedHashMap<>();
        }
        return list.stream()
                .map(this::reactionToDomain)
                .collect(Collectors.toMap(FeedbackReaction::getId, reaction -> reaction,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    // === DTO Mappings ===
    @Mapping(source = "user", target = "author") // Map domain.getUser() to FeedbackQuestionResponse.getAuthor()
    @Mapping(source = "reactions", target = "reactions") // Map domain.getReactions() to FeedbackQuestionResponse.getReactions()
    @Mapping(source = "submissionDate", target = "submissionDate")
    FeedbackQuestionResponse toResponseDto(FeedbackQuestion domain);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    FeedbackReactionResponse toResponseDto(FeedbackReaction reaction);

    List<FeedbackQuestionResponse> toResponseDto(List<FeedbackQuestion> domains);
}

