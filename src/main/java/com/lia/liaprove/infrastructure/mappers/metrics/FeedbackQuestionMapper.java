package com.lia.liaprove.infrastructure.mappers.metrics;

import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;
import com.lia.liaprove.core.domain.metrics.FeedbackReaction;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackQuestionEntity;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackReactionEntity;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.mappers.users.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class, QuestionMapper.class})
public interface FeedbackQuestionMapper {

    // === FeedbackQuestion Mappings ===

    @Mapping(target = "reactions", ignore = true) // Managed manually in the gateway
    FeedbackQuestionEntity toEntity(FeedbackQuestion domain);

    @Mapping(target = "reactionsByUser", source = "reactions")
    FeedbackQuestion toDomain(FeedbackQuestionEntity entity);

    // === FeedbackReaction Mappings ===

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
}
