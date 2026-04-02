package com.lia.liaprove.infrastructure.mappers.question;

import com.lia.liaprove.application.services.question.QuestionCreateDto;
import com.lia.liaprove.application.services.question.QuestionVotingDetails;
import com.lia.liaprove.core.domain.question.*;
import com.lia.liaprove.core.domain.user.User;
import com.lia.liaprove.core.usecases.question.PreAnalyzeQuestionUseCase;
import com.lia.liaprove.core.usecases.question.PrepareQuestionSubmissionUseCase;
import com.lia.liaprove.core.usecases.question.UpdateQuestionUseCase;
import com.lia.liaprove.infrastructure.dtos.user.AuthorDto;
import com.lia.liaprove.infrastructure.dtos.question.*;
import com.lia.liaprove.infrastructure.mappers.metrics.FeedbackQuestionMapper;
import com.lia.liaprove.infrastructure.mappers.user.UserMapper;
import com.lia.liaprove.infrastructure.entities.question.AlternativeEntity;
import com.lia.liaprove.infrastructure.entities.question.MultipleChoiceQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.ProjectQuestionEntity;
import com.lia.liaprove.infrastructure.entities.question.QuestionEntity;
import org.mapstruct.*;

import java.util.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {AlternativeMapper.class, FeedbackQuestionMapper.class, UserMapper.class})
public interface QuestionMapper {

    // ######################################## DOMAIN -> JPA #############################################

    AuthorDto toAuthorDto(User user);

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

    List<AlternativeEntity> alternativeListToEntity(List<Alternative> domainList);

    // ######################################## JPA -> DOMAIN #############################################

    @InheritInverseConfiguration(name = "toEntity")
    MultipleChoiceQuestion toDomain(MultipleChoiceQuestionEntity entity);

    @InheritInverseConfiguration(name = "toEntity")
    ProjectQuestion toDomain(ProjectQuestionEntity entity);

    default Question toDomain(QuestionEntity entity) {
        return switch (entity) {
            case null -> null;
            case MultipleChoiceQuestionEntity mc -> toDomain(mc);
            case ProjectQuestionEntity pq -> toDomain(pq);
            default -> throw new IllegalArgumentException("Unknown QuestionEntity subtype: " + entity.getClass());
        };
    }

    List<Alternative> alternativeListToDomain(List<AlternativeEntity> embList);

    // ######################################## UPDATE (MERGE) ############################################

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

    default void updateEntityFromDomain(Question domain, @MappingTarget QuestionEntity entity) {
        if (domain == null || entity == null) return;

        final String s = "Mismatched types: domain=" + domain.getClass() + " entity=" + entity.getClass();

        switch (entity) {
            case MultipleChoiceQuestionEntity mce -> {
                if (!(domain instanceof MultipleChoiceQuestion mc)) {
                    throw new IllegalArgumentException(s);
                }

                updateEntityFromDomain(mc, mce);

                List<Alternative> sourceAlternatives = mc.getAlternatives();

                if (sourceAlternatives != null) {
                    List<AlternativeEntity> convertedAlternatives = alternativeListToEntity(sourceAlternatives);

                    new ArrayList<>(mce.getAlternatives()).forEach(mce::removeAlternative);

                    if (convertedAlternatives != null) {
                        convertedAlternatives.forEach(mce::addAlternative);
                    }
                }
            }

            case ProjectQuestionEntity pqe -> {
                if (!(domain instanceof ProjectQuestion pq)) {
                    throw new IllegalArgumentException(s);
                }
                updateEntityFromDomain(pq, pqe);
            }

            default -> throw new IllegalArgumentException("Unsupported QuestionEntity subtype: " + entity.getClass()
            );
        }
    }

    @AfterMapping
    default void linkAlternativesToQuestion(@MappingTarget MultipleChoiceQuestionEntity entity) {
        if (entity.getAlternatives() != null) {
            entity.getAlternatives().forEach(alternative -> alternative.setQuestion(entity));
        }
    }

    // ######################################## REQUEST -> COMMAND ########################################

    default UpdateQuestionUseCase.UpdateQuestionCommand toUpdateCommand(UpdateQuestionRequest request) {
        if (request == null) {
            // If the whole request is null, return a command with all fields empty.
            return new UpdateQuestionUseCase.UpdateQuestionCommand(
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
            );
        }
        return new UpdateQuestionUseCase.UpdateQuestionCommand(
                Optional.ofNullable(request.title()),
                Optional.ofNullable(request.description()),
                Optional.ofNullable(request.knowledgeAreas()),
                Optional.ofNullable(request.alternatives())
        );
    }

    default PreAnalyzeQuestionUseCase.PreAnalysisCommand toPreAnalysisCommand(QuestionCreateDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("QuestionCreateDto cannot be null.");
        }

        List<String> alternatives = dto.alternatives() == null
                ? Collections.emptyList()
                : dto.alternatives().stream().map(Alternative::text).toList();

        return new PreAnalyzeQuestionUseCase.PreAnalysisCommand(
                dto.title(),
                dto.description(),
                dto.knowledgeAreas(),
                dto.difficultyByCommunity(),
                dto.relevanceByCommunity(),
                alternatives
        );
    }

    default PrepareQuestionSubmissionUseCase.PreparationCommand toPreparationCommand(
            QuestionCreateDto mappedDto, SubmitQuestionRequest request) {
        if (mappedDto == null || request == null) {
            throw new IllegalArgumentException("mappedDto and request cannot be null.");
        }

        return new PrepareQuestionSubmissionUseCase.PreparationCommand(
                mappedDto.title(),
                mappedDto.description(),
                mappedDto.knowledgeAreas(),
                mappedDto.difficultyByCommunity(),
                mappedDto.relevanceByCommunity(),
                toAlternativeInputs(mappedDto.alternatives()),
                safeList(request.getAcceptedLanguageSuggestions()),
                safeList(request.getAcceptedBiasOrAmbiguityWarnings()),
                safeList(request.getAcceptedDistractorSuggestions()),
                request.getAcceptedDifficultyLevelByLLM(),
                safeList(request.getAcceptedTopicConsistencyNotes())
        );
    }

    // ######################################## COMMAND -> DTO ############################################

    default PreAnalyzeQuestionResponse toPreAnalysisResponse(PreAnalyzeQuestionUseCase.PreAnalysisResult result) {
        if (result == null) {
            throw new IllegalArgumentException("PreAnalysisResult cannot be null.");
        }

        return new PreAnalyzeQuestionResponse(
                result.languageSuggestions(),
                result.biasOrAmbiguityWarnings(),
                result.distractorSuggestions(),
                result.difficultyLevelByLLM(),
                result.topicConsistencyNotes()
        );
    }

    default QuestionCreateDto toPreparedQuestionCreateDto(
            UUID authorId,
            QuestionCreateDto mappedDto,
            PrepareQuestionSubmissionUseCase.PreparedQuestion preparedQuestion,
            SubmitQuestionRequest request) {
        if (authorId == null || mappedDto == null || preparedQuestion == null || request == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }

        return new QuestionCreateDto(
                authorId,
                preparedQuestion.title(),
                preparedQuestion.description(),
                mappedDto.knowledgeAreas(),
                mappedDto.difficultyByCommunity(),
                mappedDto.relevanceByCommunity(),
                preparedQuestion.relevanceByLLM(),
                request instanceof SubmitProjectQuestionRequest
                        ? List.of()
                        : toAlternatives(preparedQuestion.alternatives())
        );
    }

    // ######################################## REQUEST -> DTO ############################################

    @Mapping(target = "authorId", expression = "java(authorId)")
    @Mapping(target = "relevanceByLLM", ignore = true)
    QuestionCreateDto toQuestionCreateDto(SubmitMultipleChoiceQuestionRequest req, UUID authorId);

    @Mapping(target = "authorId", expression = "java(authorId)")
    @Mapping(target = "alternatives", ignore = true)
    @Mapping(target = "relevanceByLLM", ignore = true)
    QuestionCreateDto toQuestionCreateDto(SubmitProjectQuestionRequest req, UUID authorId);

    default QuestionCreateDto toQuestionCreateDto(SubmitQuestionRequest req, UUID authorId) {
        if (req instanceof SubmitMultipleChoiceQuestionRequest mc) return toQuestionCreateDto(mc, authorId);
        if (req instanceof SubmitProjectQuestionRequest p) return toQuestionCreateDto(p, authorId);
        throw new IllegalArgumentException("Unknown SubmitQuestionRequest subtype: " + req.getClass());
    }

    // ######################################## DOMAIN -> RESPONSE DTO ####################################

    @Mapping(source = "authorId", target = "authorId")
    QuestionSummaryResponse toSummaryResponseDto(Question domain);

    @Mapping(source = "details.question.id", target = "id")
    @Mapping(source = "details.question.title", target = "title")
    @Mapping(source = "details.question.description", target = "description")
    @Mapping(source = "details.question.knowledgeAreas", target = "knowledgeAreas")
    @Mapping(source = "details.question.submissionDate", target = "submissionDate")
    @Mapping(source = "details.author", target = "author")
    @Mapping(source = "details.question", target = "alternatives")
    @Mapping(source = "details.approveVotes", target = "voteSummary.approves")
    @Mapping(source = "details.rejectVotes", target = "voteSummary.rejects")
    @Mapping(source = "details.feedbacks", target = "feedbacks")
    @Mapping(source = "details.question.relevanceByLLM", target = "relevanceByLLM")
    QuestionDetailResponse toQuestionDetailResponseDto(QuestionVotingDetails details);

    default List<AlternativeDto> mapAlternatives(Question question) {
        if (question instanceof MultipleChoiceQuestion mc) {
            List<Alternative> alts = mc.getAlternatives();
            return (alts == null) ? Collections.emptyList() : toAlternativeDtoList(alts);
        }
        return Collections.emptyList();
    }

    List<AlternativeDto> toAlternativeDtoList(List<Alternative> domainList);

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

    // ######################################## HELPERS ###################################################

    default List<String> safeList(List<String> values) {
        return values == null ? List.of() : values;
    }

    default List<PrepareQuestionSubmissionUseCase.AlternativeInput> toAlternativeInputs(List<Alternative> alternatives) {
        if (alternatives == null) return List.of();

        return alternatives.stream()
                .map(alt -> new PrepareQuestionSubmissionUseCase.AlternativeInput(alt.text(), alt.correct()))
                .toList();
    }

    default List<Alternative> toAlternatives(List<PrepareQuestionSubmissionUseCase.AlternativeInput> alternatives) {
        if (alternatives == null) return List.of();

        return alternatives.stream()
                .map(alt -> new Alternative(null, alt.text(), alt.correct()))
                .toList();
    }

    default Class<? extends QuestionEntity> mapToEntityClass(Class<? extends Question> domainClass) {
        if (MultipleChoiceQuestion.class.equals(domainClass)) {
            return MultipleChoiceQuestionEntity.class;
        } else if (ProjectQuestion.class.equals(domainClass)) {
            return ProjectQuestionEntity.class;
        }
        return QuestionEntity.class;
    }
}
