package com.lia.liaprove.infrastructure.mappers;

import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.infrastructure.entities.MultipleChoiceQuestionEntity;
import com.lia.liaprove.infrastructure.entities.ProjectQuestionEntity;
import com.lia.liaprove.infrastructure.entities.QuestionEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = {AlternativeMapper.class})
public interface QuestionMapper {
    // Dispatcher evita retorno abstrato/ambiguidade
    default QuestionEntity toEntity(Question domain) {
        return switch (domain) {
            case null -> null;
            case MultipleChoiceQuestion mc -> toEntity(mc);
            case ProjectQuestion pq -> toEntity(pq);
            default -> throw new IllegalArgumentException("Unknown Question subtype: " + domain.getClass());
        };
    }

    MultipleChoiceQuestionEntity toEntity(MultipleChoiceQuestion domain);

    ProjectQuestionEntity toEntity(ProjectQuestion domain);

    // Inversos gerados pelo MapStruct (herdam as @Mapping via @InheritInverseConfiguration)
    @InheritInverseConfiguration(name = "toEntity")
    MultipleChoiceQuestion toDomain(MultipleChoiceQuestionEntity entity);

    @InheritInverseConfiguration(name = "toEntity")
    ProjectQuestion toDomain(ProjectQuestionEntity entity);

    // Dispatcher para toDomain
    default Question toDomain(QuestionEntity entity) {
        return switch (entity) {
            case null -> null;
            case MultipleChoiceQuestionEntity mc -> toDomain(mc);
            case ProjectQuestionEntity pq -> toDomain(pq);
            default -> throw new IllegalArgumentException("Unknown QuestionEntity subtype: " + entity.getClass());
        };
    }
}
