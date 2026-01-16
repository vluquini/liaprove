package com.lia.liaprove.infrastructure.mappers.metrics;

import com.lia.liaprove.core.domain.metrics.Vote;
import com.lia.liaprove.infrastructure.dtos.metrics.VoteResponseDto;
import com.lia.liaprove.infrastructure.entities.metrics.VoteEntity;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.mappers.users.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, QuestionMapper.class})
public interface VoteMapper {

    @Mapping(source = "user", target = "user")
    @Mapping(source = "question", target = "question")
    VoteEntity toEntity(Vote domain);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "question", target = "question")
    Vote toDomain(VoteEntity entity);

    @Mapping(source = "user", target = "user") // This will use UserMapper to convert User -> UserResponseDto
    VoteResponseDto toResponseDto(Vote vote);
}
