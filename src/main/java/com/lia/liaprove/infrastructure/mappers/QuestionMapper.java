package com.lia.liaprove.infrastructure.mappers;

import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.core.domain.question.MultipleChoiceQuestion;
import com.lia.liaprove.core.domain.question.ProjectQuestion;
import com.lia.liaprove.core.domain.question.Question;
import com.lia.liaprove.infrastructure.entities.AlternativeEmbeddable;
import com.lia.liaprove.infrastructure.entities.MultipleChoiceQuestionEntity;
import com.lia.liaprove.infrastructure.entities.ProjectQuestionEntity;
import com.lia.liaprove.infrastructure.entities.QuestionEntity;
import org.mapstruct.*;

import java.util.List;

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

    // Helpers for mapping lists of alternatives (MapStruct will generate implementations)
    List<AlternativeEmbeddable> alternativeListToEntity(List<Alternative> domainList);
    List<Alternative> alternativeListToDomain(List<AlternativeEmbeddable> embList);

    // Update methods using @MappingTarget
    // Ignore fields that must be preserved by JPA (id, submissionDate, etc.)
    // Also ignore 'alternatives' here so we can control collection mutation behavior in the default dispatcher.
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "submissionDate", ignore = true)
    @Mapping(target = "recruiterUsageCount", ignore = true)
    @Mapping(target = "alternatives", ignore = true)
    void updateEntityFromDomain(MultipleChoiceQuestion domain, @MappingTarget MultipleChoiceQuestionEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "submissionDate", ignore = true)
    @Mapping(target = "recruiterUsageCount", ignore = true)
    void updateEntityFromDomain(ProjectQuestion domain, @MappingTarget ProjectQuestionEntity entity);

    /**
     * Dispatcher that safely updates a managed QuestionEntity from a domain Question.
     * - preserves JPA-managed fields (id, submissionDate, ...)
     * - ignores nulls from the source (so partial updates are possible)
     * - controls how element collections are mutated (clear + addAll) to avoid losing the entity identity
     */
    default void updateEntityFromDomain(Question domain, @MappingTarget QuestionEntity entity) {
        if (domain == null || entity == null) return;

        final String s = "Mismatched types: domain=" + domain.getClass() + " entity=" + entity.getClass();

        // Dispatch by runtime type of the entity (we are updating an existing managed entity)
        switch (entity) {
            case MultipleChoiceQuestionEntity mce -> {
                if (!(domain instanceof MultipleChoiceQuestion mc)) {
                    throw new IllegalArgumentException(s);
                }

                // First let MapStruct copy scalar properties (ignoring collections as configured)
                updateEntityFromDomain(mc, mce);

                // Handle alternatives (ElementCollection) explicitly:
                // - if source provides null -> do nothing (preserve existing list)
                // - if source provides a list -> we replace contents (clear + addAll) so Hibernate does the minimal required work
                List<Alternative> sourceAlternatives = null;
                try {
                    sourceAlternatives = mc.getAlternatives();
                } catch (ClassCastException ignored) {}

                if (sourceAlternatives != null) {
                    // convert via generated mapper method and replace
                    List<AlternativeEmbeddable> converted = alternativeListToEntity(sourceAlternatives);
                    mce.getAlternatives().clear();
                    if (converted != null) mce.getAlternatives().addAll(converted);
                }
            }

            case ProjectQuestionEntity pqe -> {
                if (!(domain instanceof ProjectQuestion pq)) {
                    throw new IllegalArgumentException(s);
                }
                updateEntityFromDomain(pq, pqe);
            }
            default -> throw new IllegalArgumentException("Unsupported QuestionEntity subtype: " + entity.getClass());
        }
    }
}
