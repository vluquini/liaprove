package com.lia.liaprove.infrastructure.mappers.metrics;

import com.lia.liaprove.core.domain.metrics.FeedbackAssessment;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackAssessmentEntity;
import com.lia.liaprove.infrastructure.mappers.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class}
)
public interface FeedbackAssessmentMapper {

    /**
     * Mapeia um objeto FeedbackAssessment de domínio para uma FeedbackAssessmentEntity JPA.
     * Utiliza UserMapper para converter o objeto de domínio User para UserEntity.
     * Mapeia o ID da tentativa para assessmentAttemptId na entidade JPA.
     *
     * @param domain O objeto FeedbackAssessment de domínio.
     * @return A entidade FeedbackAssessmentEntity correspondente.
     */
    @Mapping(target = "user", source = "user")
    @Mapping(target = "assessmentAttemptId", expression = "java(domain.getAssessmentAttempt() != null ? domain.getAssessmentAttempt().getId() : null)")
    FeedbackAssessmentEntity toEntity(FeedbackAssessment domain);

    /**
     * Mapeia uma FeedbackAssessmentEntity JPA para um objeto FeedbackAssessment de domínio.
     *
     * @param entity A entidade FeedbackAssessmentEntity.
     * @return O objeto FeedbackAssessment de domínio correspondente.
     */
    @Mapping(target = "assessmentAttempt", ignore = true)
    FeedbackAssessment toDomain(FeedbackAssessmentEntity entity);
}

