package com.lia.liaprove.infrastructure.mappers.metrics;

import com.lia.liaprove.core.domain.metrics.FeedbackAssessment;
import com.lia.liaprove.infrastructure.entities.metrics.FeedbackAssessmentEntity;
import com.lia.liaprove.infrastructure.mappers.assessment.AssessmentMapper;
import com.lia.liaprove.infrastructure.mappers.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class, AssessmentMapper.class}
)
public interface FeedbackAssessmentMapper {

    /**
     * Mapeia um objeto FeedbackAssessment de domínio para uma FeedbackAssessmentEntity JPA.
     * Utiliza UserMapper para converter o objeto de domínio User para UserEntity.
     * Mapeia o ID da avaliação para assessmentId na entidade JPA.
     *
     * @param domain O objeto FeedbackAssessment de domínio.
     * @return A entidade FeedbackAssessmentEntity correspondente.
     */
    @Mapping(target = "user", source = "user")
    @Mapping(target = "assessmentId", expression = "java(domain.getAssessment() != null ? domain.getAssessment().getId() : null)")
    FeedbackAssessmentEntity toEntity(FeedbackAssessment domain);

    /**
     * Mapeia uma FeedbackAssessmentEntity JPA para um objeto FeedbackAssessment de domínio.
     *
     * @param entity A entidade FeedbackAssessmentEntity.
     * @return O objeto FeedbackAssessment de domínio correspondente.
     */
    @Mapping(target = "assessment", ignore = true)
    FeedbackAssessment toDomain(FeedbackAssessmentEntity entity);
}

