package com.lia.liaprove.infrastructure.mappers.metrics;

import com.lia.liaprove.core.domain.metrics.QuestionVote;
import com.lia.liaprove.infrastructure.dtos.metrics.VoteResponseDto;
import com.lia.liaprove.infrastructure.entities.metrics.QuestionVoteEntity;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.mappers.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, QuestionMapper.class})
public interface VoteMapper {

    @Mapping(source = "user", target = "user")
    @Mapping(source = "question", target = "question")
    QuestionVoteEntity toEntity(QuestionVote domain);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "question", target = "question")
    QuestionVote toDomain(QuestionVoteEntity entity);

    @Mapping(source = "user", target = "user") // This will use UserMapper to convert User -> UserResponseDto
    VoteResponseDto toResponseDto(QuestionVote questionVote);
}
