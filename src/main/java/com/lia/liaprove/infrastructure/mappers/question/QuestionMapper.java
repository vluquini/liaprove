package com.lia.liaprove.infrastructure.mappers.question;

import com.lia.liaprove.application.services.question.QuestionCreateDto;
import com.lia.liaprove.core.domain.question.*;
import com.lia.liaprove.core.usecases.question.UpdateQuestionUseCase;
import com.lia.liaprove.infrastructure.dtos.question.*;
import com.lia.liaprove.infrastructure.entities.question.AlternativeEntity;
import com.lia.liaprove.infrastructure.entities.question.MultipleChoiceQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.ProjectQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    List<AlternativeEntity> alternativeListToEntity(List<Alternative> domainList);
    List<Alternative> alternativeListToDomain(List<AlternativeEntity> embList);

    // Update methods using @MappingTarget
    // Ignore fields that must be preserved by JPA (id, submissionDate, etc.)
    // Also ignore 'alternatives' here so we can control collection mutation behavior in the default dispatcher.
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "submissionDate", ignore = true)
    @Mapping(target = "votingEndDate", ignore = true)
    @Mapping(target = "recruiterUsageCount", ignore = true)
    @Mapping(target = "alternatives", ignore = true)
    void updateEntityFromDomain(MultipleChoiceQuestion domain, @MappingTarget MultipleChoiceQuestionEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "submissionDate", ignore = true)
    @Mapping(target = "votingEndDate", ignore = true)
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
                    List<AlternativeEntity> converted = alternativeListToEntity(sourceAlternatives);
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

    @AfterMapping
    default void linkAlternativesToQuestion(@MappingTarget MultipleChoiceQuestionEntity entity) {
        if (entity.getAlternatives() != null) {
            entity.getAlternatives().forEach(alternative -> alternative.setQuestion(entity));
        }
    }

    default UpdateQuestionUseCase.UpdateQuestionCommand toUpdateCommand(UpdateQuestionRequest request) {
        if (request == null) {
            // If the whole request is null, return a command with all fields empty.
            return new UpdateQuestionUseCase.UpdateQuestionCommand(Optional.empty(), Optional.empty(),
                                                    Optional.empty(), Optional.empty());
        }
        return new UpdateQuestionUseCase.UpdateQuestionCommand(
                Optional.ofNullable(request.title()),
                Optional.ofNullable(request.description()),
                Optional.ofNullable(request.knowledgeAreas()),
                Optional.ofNullable(request.alternatives())
        );
    }

    // MultipleChoice: mapeia alternativas
    @Mapping(target = "authorId", expression = "java(authorId)")
    QuestionCreateDto toQuestionCreateDto(MultipleChoiceQuestionRequest req, UUID authorId);

    // Project: não possui alternatives — ignorar esse target
    @Mapping(target = "authorId", expression = "java(authorId)")
    @Mapping(target = "alternatives", ignore = true)
    QuestionCreateDto toQuestionCreateDto(ProjectQuestionRequest req, UUID authorId);


    // Dispatcher genérico
    default QuestionCreateDto toQuestionCreateDto(QuestionRequest req, UUID authorId) {
        if (req instanceof MultipleChoiceQuestionRequest mc) return toQuestionCreateDto(mc, authorId);
        if (req instanceof ProjectQuestionRequest p) return toQuestionCreateDto(p, authorId);
        throw new IllegalArgumentException("Unknown CreateQuestionRequest subtype: " + req.getClass());
    }

    // ####################################################################################################
    // ############################# MAPPINGS FROM DOMAIN TO DTO RESPONSE (NEW) ###########################
    // ####################################################################################################

    MultipleChoiceQuestionResponse toResponseDto(MultipleChoiceQuestion domain);

    ProjectQuestionResponse toResponseDto(ProjectQuestion domain);

    default QuestionResponse toResponseDto(Question domain) {
        return switch (domain) {
            case null -> null;
            case MultipleChoiceQuestion mc -> toResponseDto(mc);
            case ProjectQuestion pq -> toResponseDto(pq);
            default -> throw new IllegalArgumentException("Unknown Question subtype: " + domain.getClass().getName());
        };

    }
}
