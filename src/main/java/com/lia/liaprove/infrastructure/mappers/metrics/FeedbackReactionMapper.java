package com.lia.liaprove.infrastructure.mappers.metrics;

import com.lia.liaprove.core.domain.metrics.FeedbackReaction;
import com.lia.liaprove.infrastructure.dtos.metrics.FeedbackReactionResponse;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackReactionEntity;
import com.lia.liaprove.infrastructure.mappers.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface FeedbackReactionMapper {

    @Mapping(target = "feedback", ignore = true)
    FeedbackReactionEntity toEntity(FeedbackReaction domain);

    @Mapping(target = "feedback", ignore = true)
    FeedbackReaction toDomain(FeedbackReactionEntity entity);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    FeedbackReactionResponse toResponseDto(FeedbackReaction reaction);

    List<FeedbackReaction> toDomainList(List<FeedbackReactionEntity> entities);

    List<FeedbackReactionResponse> toResponseDto(List<FeedbackReaction> reactions);
}
