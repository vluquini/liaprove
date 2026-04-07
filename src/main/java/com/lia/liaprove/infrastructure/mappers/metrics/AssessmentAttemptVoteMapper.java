package com.lia.liaprove.infrastructure.mappers.metrics;

import com.lia.liaprove.core.domain.metrics.AssessmentAttemptVote;
import com.lia.liaprove.infrastructure.entities.metrics.AssessmentAttemptVoteEntity;
import com.lia.liaprove.infrastructure.mappers.assessment.AssessmentAttemptMapper;
import com.lia.liaprove.infrastructure.mappers.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, AssessmentAttemptMapper.class})
public interface AssessmentAttemptVoteMapper {

    @Mapping(source = "user", target = "user")
    @Mapping(source = "assessmentAttempt", target = "assessmentAttempt")
    AssessmentAttemptVoteEntity toEntity(AssessmentAttemptVote domain);
}
