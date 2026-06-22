package com.lia.liaprove.infrastructure.mappers.metrics;

import com.lia.liaprove.core.domain.metrics.FeedbackQuestion;
import com.lia.liaprove.infrastructure.dtos.metrics.FeedbackQuestionResponse;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackQuestionEntity;
import com.lia.liaprove.infrastructure.mappers.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, FeedbackReactionMapper.class})
public interface FeedbackQuestionMapper {

    // === FeedbackQuestion Mappings ===

    @Mapping(target = "reactions", ignore = true) // Managed manually in the gateway
    @Mapping(target = "question", ignore = true)
    FeedbackQuestionEntity toEntity(FeedbackQuestion domain);

    @Mapping(target = "question", ignore = true)
    FeedbackQuestion toDomain(FeedbackQuestionEntity entity);

    // === DTO Mappings ===
    @Mapping(source = "user", target = "author") // Map domain.getUser() to FeedbackQuestionResponse.getAuthor()
    @Mapping(source = "reactions", target = "reactions") // Map domain.getReactions() to FeedbackQuestionResponse.getReactions()
    @Mapping(source = "submissionDate", target = "submissionDate")
    FeedbackQuestionResponse toResponseDto(FeedbackQuestion domain);

    List<FeedbackQuestionResponse> toResponseDto(List<FeedbackQuestion> domains);
}

