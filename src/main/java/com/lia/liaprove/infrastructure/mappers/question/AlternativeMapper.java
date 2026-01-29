package com.lia.liaprove.infrastructure.mappers.question;

import com.lia.liaprove.core.domain.question.Alternative;
import com.lia.liaprove.infrastructure.dtos.question.AlternativeDto;
import com.lia.liaprove.infrastructure.dtos.question.AlternativeRequestDto;
import com.lia.liaprove.infrastructure.entities.question.AlternativeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AlternativeMapper {

    Alternative toDomain(AlternativeEntity entity);

    @Mapping(target = "id", ignore = true)
    Alternative toDomain(AlternativeRequestDto dto);

    @Mapping(target = "id", ignore = true) // A entidade pai (Question) será setada pelo QuestionMapper
    @Mapping(target = "question", ignore = true)
    AlternativeEntity toEntity(Alternative domain);

    AlternativeDto toAlternativeDto(Alternative domain);
//    List<AlternativeDto> toAlternativeDtoList(List<Alternative> domainList);
}
