package com.lia.liaprove.infrastructure.mappers.assessment;

import com.lia.liaprove.core.domain.assessment.AssessmentAttempt;
import com.lia.liaprove.infrastructure.entities.assessment.AnswerEntity;
import com.lia.liaprove.infrastructure.entities.assessment.AssessmentAttemptEntity;
import com.lia.liaprove.infrastructure.mappers.question.QuestionMapper;
import com.lia.liaprove.infrastructure.mappers.users.UserMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {AssessmentMapper.class, AnswerMapper.class, CertificateMapper.class, QuestionMapper.class, UserMapper.class}
)
public interface AssessmentAttemptMapper {

    @Mapping(target = "answers", source = "answers")
    AssessmentAttemptEntity toEntity(AssessmentAttempt domain);

    @Mapping(target = "feedbacks", expression = "java(new java.util.ArrayList<>())")
    AssessmentAttempt toDomain(AssessmentAttemptEntity entity);

    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "answers", ignore = true)
    @Mapping(target = "feedbacks", expression = "java(new java.util.ArrayList<>())")
    AssessmentAttempt toDomainSummary(AssessmentAttemptEntity entity);

    @AfterMapping
    default void linkAnswers(@MappingTarget AssessmentAttemptEntity entity) {
        if (entity == null || entity.getAnswers() == null) {
            return;
        }
        for (AnswerEntity answer : entity.getAnswers()) {
            answer.setAttempt(entity);
        }
    }
}

