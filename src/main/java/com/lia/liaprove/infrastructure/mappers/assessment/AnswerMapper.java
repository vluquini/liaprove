package com.lia.liaprove.infrastructure.mappers.assessment;

import com.lia.liaprove.core.domain.assessment.Answer;
import com.lia.liaprove.infrastructure.entities.assessment.AnswerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnswerMapper {

    @Mapping(target = "attempt", ignore = true)
    AnswerEntity toEntity(Answer domain);

    default Answer toDomain(AnswerEntity entity) {
        if (entity == null) {
            return null;
        }

        Answer answer = new Answer(entity.getQuestionId());
        answer.setSelectedAlternativeId(entity.getSelectedAlternativeId());
        answer.setProjectUrl(entity.getProjectUrl());
        return answer;
    }
}

